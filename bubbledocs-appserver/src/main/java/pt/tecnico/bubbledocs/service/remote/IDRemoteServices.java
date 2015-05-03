package pt.tecnico.bubbledocs.service.remote;

import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.ServiceLookupException;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IDRemoteServices extends SDRemoteServices implements SDId {
    final private String uddiURL = "http://localhost:8081";
    final private String serviceName = "SD-ID";

    /** WS service */
    protected SDId_Service service = null;

    /** WS port (interface) */
    protected SDId port = null;

    public IDRemoteServices() {
        setVerbose(false);
        try {
            lookForService(uddiURL, serviceName);
            createStub();
        } catch (ServiceLookupException e) {
            throw new RemoteInvocationException();
        }
    }

    @Override
    protected void createStub() {
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

    public SDId_Service getService() {
        return service;
    }

    public void setService(SDId_Service service) {
        this.service = service;
    }

    public SDId getPort() {
        return port;
    }

    public void setPort(SDId port) {
        this.port = port;
    }

    public String getUddiURL() {
        return uddiURL;
    }

    public String getServiceName() {
        return serviceName;
    }

    /*
     *  Remote Interface (non-Javadoc)
     */

    @Override
    public void createUser(String username, String email) throws InvalidUsernameException, DuplicateUsernameException,
            DuplicateEmailException, InvalidEmailException, RemoteInvocationException {
        try {
            port.createUser(username, email);
        } catch (EmailAlreadyExists_Exception e) {
            throw new DuplicateEmailException();
        } catch (InvalidEmail_Exception e) {
            throw new InvalidEmailException("The email: " + email + " is invalid.");
        } catch (InvalidUser_Exception e) {
            throw new InvalidUsernameException("The username: " + username + " is invalid.");
        } catch (UserAlreadyExists_Exception e) {
            throw new DuplicateUsernameException();
        }
    }

    public void loginUser(String username, String password) throws LoginBubbleDocsException, RemoteInvocationException {
        if (password == null || username == null) {
            throw new LoginBubbleDocsException();
        }
        try {
            requestAuthentication(username, password.getBytes());
        } catch (AuthReqFailed_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

    @Override
    public void removeUser(String username) throws LoginBubbleDocsException, RemoteInvocationException {
        try {
            port.removeUser(username);
        } catch (UserDoesNotExist_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

    @Override
    public void renewPassword(String username) throws LoginBubbleDocsException, RemoteInvocationException {
        try {
            port.renewPassword(username);
        } catch (UserDoesNotExist_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

    @Override
    public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception {
        return port.requestAuthentication(userId, reserved);
    }
}