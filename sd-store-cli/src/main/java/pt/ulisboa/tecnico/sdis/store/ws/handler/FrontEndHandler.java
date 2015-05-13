package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.util.Iterator;
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

/**
 * This SOAPHandler shows how to set/get values from headers in
 * inbound/outbound SOAP messages.
 *
 * A header is created in an outbound message and is read on an
 * inbound message.
 *
 * The value that is read from the header
 * is placed in a SOAP message context property
 * that can be accessed by other handlers or by the application.
 */
public class FrontEndHandler implements SOAPHandler<SOAPMessageContext> {

    /* 
     * Handler interface methods:
     * - getHeaders()
     * - handleMesasge(SOAPMessageContext)
     * - handleFault(SOAPMessageContext)
     * - close(MessageContext)
     */

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                if (smc.get("requestTag") != null) {
                    if ((boolean) smc.get("requestTag")) {
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
                            Name name = soapEnvelope.createName("requestTag", "rt", "http://requestTag");
                            SOAPElement element = soapHeader.addHeaderElement(name);

                            // Add header element value
                            boolean doTagHeader = true;
                            String value = new Boolean(doTagHeader).toString();
                            element.addTextNode(value);

                        } catch (SOAPException e) {
                            System.out.printf("Failed to add SOAP header because of %s%n", e);
                        }
                    }
                } else if (smc.get("newTag") != null) {
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
                        Name name = soapEnvelope.createName("newTag", "nt", "http://newTag");
                        SOAPHeaderElement element = soapHeader.addHeaderElement(name);

                        // Add header element value
                        int tag = (int) smc.get("newTag");
                        String value = new Integer(tag).toString();
                        element.addTextNode(value);
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

                // Get first header element
                Name name = soapEnvelope.createName("tag", "t", "http://tag");
                Iterator<?> it = soapHeader.getChildElements(name);
                // Check header element
                if (!it.hasNext()) {
                    name = soapEnvelope.createName("ack", "a", "http://ack");
                    it = soapHeader.getChildElements(name);

                    if (!it.hasNext()) {
                        System.out.println("Header element not found.");
                        return true;
                    }

                    SOAPElement element = (SOAPElement) it.next();

                    // Get header element value
                    String valueString = element.getValue();
                    boolean value = Boolean.parseBoolean(valueString);

                    // Print received header
                    System.out.println("Header value is " + value);

                    // Put header in a property context
                    smc.put("ack", value);
                    // Set property scope to application client/server class can access it
                    smc.setScope("ack", Scope.APPLICATION);
                } else {
                    SOAPElement element = (SOAPElement) it.next();
                    // Get header element value
                    String valueString = element.getValue();
                    int value = Integer.parseInt(valueString);

                    // Print received header
                    System.out.println("Header value is " + value);

                    // Put header in a property context
                    smc.put("tag", value);
                    // Set property scope to application client/server class can access it
                    smc.setScope("tag", Scope.APPLICATION);

                }
            }
        } catch (Exception e) {
            System.out.print("Caught exception in handleMessage: ");
            System.out.println(e);
            System.out.println("Continue normal processing...");
        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        System.out.println("Ignoring fault message...");
        return true;
    }

    public void close(MessageContext messageContext) {
    }

}