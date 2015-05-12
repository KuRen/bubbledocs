package pt.tecnico.bubbledocs.service.remote;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.ServiceLookupException;
import pt.tecnico.bubbledocs.service.dto.AuthenticationResult;
import pt.ulisboa.tecnico.sdis.id.client.IdClient;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IDRemoteServices {

    private IdClient client;
    private final static String uddiURL = "http://localhost:8081";
    private final static String serviceName = "SD-ID";

    public IDRemoteServices() {
        try {
            IdClient client = new IdClient(uddiURL, serviceName);
            this.client = client;
        } catch (Exception e) {
            // We are very optimistic. This will never happen :)
            throw new ServiceLookupException();
        }
    }

    public IDRemoteServices(IdClient client) {
        super();
        this.client = client;
    }

    public void createUser(String username, String email) throws InvalidUsernameException, DuplicateUsernameException,
            DuplicateEmailException, InvalidEmailException, RemoteInvocationException {
        try {
            client.createUser(username, email);
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

    public AuthenticationResult loginUser(String username, String password) throws LoginBubbleDocsException,
            RemoteInvocationException {
        if (password == null || username == null) {
            throw new LoginBubbleDocsException();
        }
        try {
            byte[] xmlBytes = client.requestAuthentication(username, password.getBytes());

            SAXBuilder builder = new SAXBuilder();
            builder.setIgnoringElementContentWhitespace(true);

            try {
                Document xmlDocument = builder.build(new ByteArrayInputStream(xmlBytes));
                String key = xmlDocument.getRootElement().getChildText("ClientServerKey");
                String ticket = xmlDocument.getRootElement().getChildText("Ticket");
                return new AuthenticationResult(key, ticket);
            } catch (JDOMException | IOException e) {
                throw new LoginBubbleDocsException();
            }

        } catch (AuthReqFailed_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

    public void removeUser(String username) throws LoginBubbleDocsException, RemoteInvocationException {
        try {
            client.removeUser(username);
        } catch (UserDoesNotExist_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

    public void renewPassword(String username) throws LoginBubbleDocsException, RemoteInvocationException {
        try {
            client.renewPassword(username);
        } catch (UserDoesNotExist_Exception e) {
            throw new LoginBubbleDocsException();
        }
    }

}