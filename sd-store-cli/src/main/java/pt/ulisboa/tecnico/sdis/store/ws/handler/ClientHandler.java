package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.io.StringWriter;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import pt.ulisboa.tecnico.sdis.store.ws.client.StoreCrypto;

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
            if (!outboundElement.booleanValue()) {
                System.out.println("Reading header in outbound SOAP message...");
                try {
                    // Get SOAP envelope
                    SOAPMessage message = smc.getMessage();
                    SOAPPart soapPart = message.getSOAPPart();
                    SOAPEnvelope soapEnvelope = soapPart.getEnvelope();
                    StoreCrypto storeCrypto = new StoreCrypto();

                    SOAPBody soapBody = soapEnvelope.getBody();
                    DOMSource source = new DOMSource(soapBody);
                    StringWriter stringResult = new StringWriter();
                    TransformerFactory.newInstance().newTransformer().transform(source, new StreamResult(stringResult));
                    String result = stringResult.toString();

                    String digest = storeCrypto.mac(result.getBytes());
                    String key = storeCrypto.generateKCS();
                    storeCrypto.encrypt(digest, key);
                    storeCrypto.encrypt(result, key);

                    // Add header
                    SOAPHeader soapHeader = soapEnvelope.getHeader();
                    if (soapHeader == null)
                        soapHeader = soapEnvelope.addHeader();

                    // Add header element (name, namespace prefix, namespace)
                    Name name = soapEnvelope.createName("mac", "mac", "http://mac");
                    SOAPElement element = soapHeader.addHeaderElement(name);

                    // Add header element value
                    boolean doTagHeader = true;
                    String value = new Boolean(doTagHeader).toString();
                    element.addTextNode(value);

                } catch (SOAPException e) {
                    System.out.printf("Failed to add SOAP header because of %s%n", e);
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
        return true; //TODO
    }

    public void close(MessageContext messageContext) {

    }

}