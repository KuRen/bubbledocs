package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
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
public class ClientHandler implements SOAPHandler<SOAPMessageContext> {

    //
    // Handler interface methods
    //
    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundElement.booleanValue()) {
                boolean requestTag = (boolean) smc.get("requestTag");
                if(requestTag) {
                    System.out.println("Reading header in outbound SOAP message...");
    
                    // get SOAP envelope
                    SOAPMessage msg = smc.getMessage();
                    SOAPPart sp = msg.getSOAPPart();
                    SOAPEnvelope se = sp.getEnvelope();
    
                    // add header
                    SOAPHeader sh = se.getHeader();
                    if (sh == null)
                        sh = se.addHeader();
    
                    // add header element (name, namespace prefix, namespace)
                    Name name = se.createName("Tag", "t", "http://Tag");
                    SOAPHeaderElement element = sh.addHeaderElement(name);
    
                    // add header element value
                    int tag = (int) smc.get("Tag");
                    String valueString = Integer.toString(tag);
                    element.addTextNode(valueString);
                }
            } else {
                System.out.println("Reading header in inbound SOAP message...");

                // get SOAP envelope header
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();
                SOAPHeader sh = se.getHeader();

                // check header
                if (sh == null) {
                    System.out.println("Header not found.");
                    return true;
                }

                // get first header element
                Name name = se.createName("requestTag", "et", "http://requestTag");
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.println("Header element not found.");
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // get header element value
                String valueString = element.getValue();
                boolean value = Boolean.parseBoolean(valueString);

                // print received header
                System.out.println("Header value is " + value);

                // put header in a property context
                smc.put("requestTag", value);
                // set property scope to application client/server class can access it
                smc.setScope("requestTag", Scope.APPLICATION);
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