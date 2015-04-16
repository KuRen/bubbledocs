package pt.ulisboa.tecnico.sdis.id.client;

import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.id.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClient implements SDId {
    /** WS service */
    SDId_Service service = null;

    /** WS port (interface) */
    SDId port = null;

    /** Endpoint URL */
    private String URL = null;

    /** output option **/
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * constructor with provided web service URL
     * 
     * @throws JAXRException
     */
    public IdClient() throws JAXRException {

    }

    public void createStub() {
        if (verbose)
            System.out.println("Creating stub ...");

        service = new SDId_Service();
        port = service.getSDIdImplPort();

        if (verbose)
            System.out.println("Setting endpoint address ...");

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
    }

    public void lookForService(String uddiURL, String serviceName) throws JAXRException {
        if (verbose)
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        if (verbose)
            System.out.printf("Looking for '%s'%n", serviceName);
        URL = uddiNaming.lookup(serviceName);

        if (URL == null && verbose) {
            System.out.println("Not found!");
            return;
        } else {
            if (verbose)
                System.out.printf("Found %s%n", URL);
        }
    }

    // SDId

    @Override
    public void createUser(String userId, String emailAddress) throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {

        port.createUser(userId, emailAddress);

    }

    @Override
    public void renewPassword(String userId) throws UserDoesNotExist_Exception {
        port.renewPassword(userId);

    }

    @Override
    public void removeUser(String userId) throws UserDoesNotExist_Exception {
        port.removeUser(userId);

    }

    @Override
    public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception {
        return port.requestAuthentication(userId, reserved);
    }
}