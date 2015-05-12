package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.util.Set;

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

public class AuthenticationHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String TICKET_PROPERTY = "authenticationHeader";

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
                System.out.println("Reading header in inbound SOAP message...");

                // Get SOAP envelope header
                SOAPMessage message = smc.getMessage();
                SOAPPart soapPart = message.getSOAPPart();
                SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                SOAPHeader soapHeader = soapEnvelope.getHeader();

                // Check header
                if (soapHeader == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // Get ticket header element
                Name name = soapEnvelope.createName("authenticationTicket", "at", "http://authenticationTicket");
                SOAPElement element = (SOAPElement) soapHeader.getChildElements(name).next();

                String valueString = element.getValue();

                // Put header in a property context
                smc.put("at", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("at", Scope.APPLICATION);

                // Get authentication header element
                name = soapEnvelope.createName("authentication", "auth", "http://authentication");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                valueString = element.getValue();

                // Put header in a property context
                smc.put("auth", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("auth", Scope.APPLICATION);

                // Get nonce header element
                name = soapEnvelope.createName("authenticationNonce", "nonce", "http://authenticationNonce");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                valueString = element.getValue();

                // Put header in a property context
                smc.put("nonce", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("nonce", Scope.APPLICATION);

                // Get hash header element
                name = soapEnvelope.createName("authenticationHash", "hash", "http://authenticationHash");
                element = (SOAPElement) soapHeader.getChildElements(name).next();

                valueString = element.getValue();

                // Put header in a property context
                smc.put("hash", valueString);
                // Set property scope to application client/server class can access it
                smc.setScope("hash", Scope.APPLICATION);

            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
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

}
