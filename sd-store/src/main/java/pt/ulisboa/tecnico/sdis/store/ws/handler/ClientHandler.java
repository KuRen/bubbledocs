package pt.ulisboa.tecnico.sdis.store.ws.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
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
        return true; //TODO
    }

    public boolean handleFault(SOAPMessageContext smc) {
        return true; //TODO
    }

    public void close(MessageContext messageContext) {

    }

}