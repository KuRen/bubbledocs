package pt.ulisboa.tecnico.sdis.store.ws.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
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
                        Name name = soapEnvelope.createName("authenticationTicket", "at", "http://authenticationTicket");
                        SOAPHeaderElement element = soapHeader.addHeaderElement(name);

                        // Add header element value
                        String ticket = (String) smc.get("authenticationTicket");
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

                message.writeTo(System.out);

                // Check header
                if (soapHeader == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                for (String string : smc.keySet())
                    System.out.printf("%s : %s%n", string, smc.get(string));

                System.out.println(" > > Header attributes");

                Iterator<Name> iterator = soapHeader.getAllAttributes();
                while (iterator.hasNext()) {
                    Name name = iterator.next();
                    System.out.printf("Header : %s = %s%n", name.toString(), soapHeader.getAttributeValue(name));
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
/*
                // Get nonce header element
                name = soapEnvelope.createName("authenticationNonce", "nonce", "http://authenticationNonce");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                valueString = element.getValue();

                // Put header in a property context
                smc.put("nonce", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("nonce", Scope.APPLICATION);
*/
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
            //System.out.println("Continue normal processing...");
        }

        return true;
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
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return false; //FIXME throw exception
        }

        byte[] secretServerKey = "SecretSecretSecretSecretSecretSecretSecret".getBytes();

        SecretKeyFactory factory;
        SecretKey key = null;
        try {
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(new DESKeySpec(secretServerKey));
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e1) {
            return false; //FIXME throw exception
        }

        try {
            System.out.println("==========CHIPH TIVKET::: " + chipheredTicket);
            cipher.init(Cipher.DECRYPT_MODE, key);
            ticketBytes = cipher.doFinal(parseBase64Binary(chipheredTicket));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            return false; //FIXME throw exception
        }

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);
        Document ticket = null;

        try {
            ticket = builder.build(new ByteArrayInputStream(ticketBytes));
        } catch (JDOMException | IOException e) {
            return false; //FIXME throw exception
        }

        DateTime expire = new DateTime(ticket.getRootElement().getChildText("Expiration"));
        DateTime startTime = new DateTime(ticket.getRootElement().getChildText("StartTime"));
        DateTime now = new DateTime();

        if (startTime.getMillis() >= now.getMillis() || expire.getMillis() <= now.getMillis()) {
            //return false;
        }

        clientServerKey = ticket.getRootElement().getChildText("Key");

        System.out.println("CSKey: " + clientServerKey);

        try {
            key = factory.generateSecret(new DESKeySpec(clientServerKey.getBytes()));
        } catch (InvalidKeyException | InvalidKeySpecException e) {
            return false; //FIXME throw exception
        }

        System.out.println("Made KCS");

        byte[] authBytes = null;

        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            authBytes = cipher.doFinal(parseBase64Binary(cipherAuth));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            e.printStackTrace();
            return false; //FIXME throw exception
        }

        System.out.println("Made authBytes!");

        Document auth = null;

        try {
            auth = builder.build(new ByteArrayInputStream(authBytes));
        } catch (JDOMException | IOException e) {
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
