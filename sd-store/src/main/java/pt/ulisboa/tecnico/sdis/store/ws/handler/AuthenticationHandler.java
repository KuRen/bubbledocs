package pt.ulisboa.tecnico.sdis.store.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.MessageContext.Scope;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.joda.time.DateTime;

public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String TICKET_PROPERTY = "authenticationHeader";

    private String clientServerKey;

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                if (smc.get("authenticationHeader") != null) {
                    System.out.println("Writing header in outbound SOAP message...");
                    try {
                        // Get SOAP envelope
                        SOAPMessage message = smc.getMessage();
                        SOAPPart soapPart = message.getSOAPPart();
                        SOAPEnvelope soapEnvelope = soapPart.getEnvelope();

                        // Add header
                        SOAPHeader soapHeader = soapEnvelope.getHeader();
                        if (soapHeader == null)
                            soapHeader = soapEnvelope.addHeader();

                        // Add header element (name, namespace prefix, namespace)
                        Name name = soapEnvelope.createName("myResponseHeader", "e", "urn:response");
                        SOAPHeaderElement element = soapHeader.addHeaderElement(name);

                        // Add header element value
                        String ticket = makeResponse((String) smc.get("outNonce"));
                        element.addTextNode(ticket);
                    } catch (SOAPException e) {
                        System.out.printf("Failed to add SOAP header because of %s%n", e);
                    }
                }
            } else {
                System.out.println();
                System.out.println(" > > > Reading header in inbound SOAP message...");

                // Get SOAP envelope header
                SOAPMessage message = smc.getMessage();
                SOAPPart soapPart = message.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                SOAPHeader soapHeader = soapEnvelope.getHeader();

                //message.writeTo(System.out);

                // Check header
                if (soapHeader == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // Get ticket header element
                Name name = soapEnvelope.createName("authenticationTicket", "e", "urn:at");
                SOAPElement element = (SOAPElement) soapHeader.getChildElements(name).next();

                String cipherTicket = element.getValue();

                System.out.println("AUTH HANDLER ::: Ciph Ticket == " + cipherTicket);

                // Put header in a property context
                smc.put("at", cipherTicket);
                // Set property scope to application client/server class can access it
                smc.setScope("at", Scope.APPLICATION);

                // Get authentication header element
                name = soapEnvelope.createName("authentication", "e", "urn:auth");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                String cipherAuth = element.getValue();

                System.out.println("AUTH HANDLER ::: Auth == " + cipherAuth);

                // Put header in a property context
                smc.put("auth", cipherAuth);
                // Set property scope to application client/server class can access it
                smc.setScope("auth", Scope.APPLICATION);

                if (!authenticate(cipherTicket, cipherAuth))
                    System.out.println("Could not auth! Bad Credentials! Abort! Trap!");

                // Get nonce header element
                name = soapEnvelope.createName("authenticationNonce", "nonce", "http://authenticationNonce");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                String nonce = element.getValue();

                // Put header in a property context
                smc.put("nonce", nonce);
                // Set property scope to application client/server class can access it
                smc.setScope("nonce", Scope.APPLICATION);

                /*
                // Get hash header element
                name = soapEnvelope.createName("authenticationHash", "hash", "http://authenticationHash");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                valueString = element.getValue();

                // Put header in a property context
                smc.put("hash", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("hash", Scope.APPLICATION);
                */
            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            e.printStackTrace();
            //System.out.println("Continue normal processing...");
        }

        return true;
    }

    private String makeResponse(String nonce) {
        byte[] bytes = nonce.getBytes();
        Key key = new SecretKeySpec(clientServerKey.getBytes(), "AES");

        // get a AES cipher object
        Cipher cipher;
        byte[] cipherBytes = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // encrypt using the key and the plaintext
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            cipherBytes = cipher.doFinal(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        return printBase64Binary(cipherBytes);
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    @Override
    public void close(MessageContext context) {
    }

    @Override
    public Set<QName> getHeaders() {
        return null;
    }

    private boolean authenticate(String chipheredTicket, String cipherAuth) {
        byte[] ticketBytes = null;

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return false; //FIXME throw exception
        }

        byte[] secretServerKey = "SecretSecretSecr".getBytes();

        SecretKey key = null;
        key = new SecretKeySpec(secretServerKey, "AES");

        try {
            System.out.println("==========CHIPH TIVKET::: " + chipheredTicket);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            ticketBytes = cipher.doFinal(parseBase64Binary(chipheredTicket));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return false; //FIXME throw exception
        }

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);
        Document ticket = null;

        try {
            ticket = builder.build(new ByteArrayInputStream(ticketBytes));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            return false; //FIXME throw exception
        }

        DateTime expire = new DateTime(ticket.getRootElement().getChildText("Expiration"));
        DateTime startTime = new DateTime(ticket.getRootElement().getChildText("StartTime"));
        DateTime now = new DateTime();

        if (startTime.getMillis() >= now.getMillis() || expire.getMillis() <= now.getMillis()) {
            return false;
        }

        clientServerKey = ticket.getRootElement().getChildText("Key");

        System.out.println("CSKey: " + clientServerKey + " Key is something like: "
                + new String(parseBase64Binary(clientServerKey)));

        key = new SecretKeySpec(parseBase64Binary(clientServerKey), "AES");

        System.out.println("Made KCS with ecrypted: " + clientServerKey);

        byte[] authBytes = null;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[16]));
            authBytes = cipher.doFinal(parseBase64Binary(cipherAuth));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return false; //FIXME throw exception
        }

        System.out.println("Made authBytes!");

        Document auth = null;

        try {
            auth = builder.build(new ByteArrayInputStream(authBytes));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            return false; //FIXME throw exception
        }

        System.out.println("Auth user:: " + auth.getRootElement().getChildText("User") + " | Ticket User:: "
                + ticket.getRootElement().getChildText("Client"));

        if (!auth.getRootElement().getChildText("User").equals(ticket.getRootElement().getChildText("Client"))) {
            return false;
        }

        /* TIME?
        if (!auth.getRootElement().getChildText("Time").equals(ticket.getRootElement().getChildText("Client"))){
            return false;
        }*/

        return true;
    }

}
