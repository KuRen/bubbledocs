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
 * This is the handler client class of the Relay example.
 *
 * #2 The client handler receives data from the client (via message context).
 * #3 The client handler passes data to the server handler (via outbound SOAP message header).
 *
 * *** GO TO server handler to see what happens next! ***
 *
 * #10 The client handler receives data from the server handler (via inbound SOAP message header).
 * #11 The client handler passes data to the client (via message context).
 *
 * *** GO BACK TO client to see what happens next! ***
 */

public class RelayClientHandler implements SOAPHandler<SOAPMessageContext> {

    public static final String REQUEST_TICKET = "my.request.ticket";

    public static final String REQUEST_TICKET_HEADER = "authenticationTicket";
    public static final String REQUEST_TICKET_NS = "urn:at";

    public static final String REQUEST_AUTH = "my.request.auth";

    public static final String REQUEST_AUTH_HEADER = "authentication";
    public static final String REQUEST_AUTH_NS = "urn:auth";

    public static final String REQUEST_NONCE = "my.request.nonce";

    public static final String REQUEST_NONCE_HEADER = "authenticationNonce";
    public static final String REQUEST_NONCE_NS = "urn:nonce";

    public static final String REQUEST_HASH = "my.request.hash";

    public static final String REQUEST_HASH_HEADER = "authenticationHash";
    public static final String REQUEST_HASH_NS = "urn:hash";

    public static final String RESPONSE_HEADER = "myResponseHeader";
    public static final String RESPONSE_NS = "urn:response";

    public static final String CLASS_NAME = RelayClientHandler.class.getSimpleName();
    public static final String TOKEN = "client-handler";

    public boolean handleMessage(SOAPMessageContext smc) {
        Boolean outbound = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outbound) {
            // outbound message

            // *** #2 ***
            // get token from request context
            String propertyValue = (String) smc.get(REQUEST_TICKET);
            System.out.printf("%s received '%s'%n", CLASS_NAME, propertyValue);

            // put token in request SOAP header
            try {
                // get SOAP envelope
                SOAPMessage msg = smc.getMessage();
                SOAPPart sp = msg.getSOAPPart();
                SOAPEnvelope se = sp.getEnvelope();

                // add header
                SOAPHeader sh = se.getHeader();
                if (sh == null)
                    sh = se.addHeader();

                // TICKET
                // add header element (name, namespace prefix, namespace)
                Name name = se.createName(REQUEST_TICKET_HEADER, "e", REQUEST_TICKET_NS);
                SOAPHeaderElement element = sh.addHeaderElement(name);

                // *** #3 ***
                // add header element value
                String newValue = propertyValue + "," + TOKEN;
                element.addTextNode(newValue);

                System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, newValue);

                // AUTH
                String auth = (String) smc.get(REQUEST_AUTH);

                // add header element (name, namespace prefix, namespace)
                name = se.createName(REQUEST_AUTH_HEADER, "e", REQUEST_AUTH_NS);
                element = sh.addHeaderElement(name);

                // *** #3 ***
                // add header element value
                element.addTextNode(auth);

                System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, auth);

                // NONCE
                String nonce = (String) smc.get(REQUEST_NONCE);

                // add header element (name, namespace prefix, namespace)
                name = se.createName(REQUEST_NONCE_HEADER, "e", REQUEST_NONCE_NS);
                element = sh.addHeaderElement(name);

                // *** #3 ***
                // add header element value
                element.addTextNode(nonce);

                System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, nonce);

                // HASH
                String hash = (String) smc.get(REQUEST_HASH);

                // add header element (name, namespace prefix, namespace)
                name = se.createName(REQUEST_HASH_HEADER, "e", REQUEST_HASH_NS);
                element = sh.addHeaderElement(name);

                // *** #3 ***
                // add header element value
                element.addTextNode(hash);

                System.out.printf("%s put token '%s' on request message header%n", CLASS_NAME, hash);

            } catch (SOAPException e) {
                System.out.printf("Failed to add SOAP header because of %s%n", e);
            }

        } else {
            // inbound message

            // get token from response SOAP header
            try {
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
                Name name = se.createName(RESPONSE_HEADER, "e", RESPONSE_NS);
                Iterator it = sh.getChildElements(name);
                // check header element
                if (!it.hasNext()) {
                    System.out.printf("Header element %s not found.%n", RESPONSE_HEADER);
                    return true;
                }
                SOAPElement element = (SOAPElement) it.next();

                // *** #10 ***
                // get header element value
                String headerValue = element.getValue();
                System.out.printf("%s got '%s'%n", CLASS_NAME, headerValue);

                // *** #11 ***
                // put token in response context
                String newValue = headerValue + "," + TOKEN;
                System.out.printf("%s put token '%s' on response context%n", CLASS_NAME, TOKEN);
                smc.put(RESPONSE_HEADER, newValue);
                // set property scope to application so that client class can access property
                smc.setScope(RESPONSE_HEADER, Scope.APPLICATION);

            } catch (SOAPException e) {
                System.out.printf("Failed to get SOAP header because of %s%n", e);
            }

        }

        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
        return true;
    }

    public Set<QName> getHeaders() {
        return null;
    }

    public void close(MessageContext messageContext) {
    }

}
