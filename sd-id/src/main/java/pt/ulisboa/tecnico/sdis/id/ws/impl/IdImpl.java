package pt.ulisboa.tecnico.sdis.id.ws.impl;

import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

@SuppressWarnings("restriction")
@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.id.ws.SDId", wsdlLocation = "SD-ID.1_1.wsdl", name = "SdId",
        portName = "SDIdImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", serviceName = "SDId")
public class IdImpl implements SDId {

    public void createUser(String userId, String emailAddress) throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        UserManager userManager = UserManager.getInstance();
        User user = userManager.create(userId, emailAddress);
        System.out.printf("User %s created with password: %s%n", user.getUsername(), user.getPassword());
    }

    public void renewPassword(String userId) throws UserDoesNotExist_Exception {
        UserManager userManager = UserManager.getInstance();
        User user = userManager.renewPassword(userId);
        System.out.printf("User %s password renewed to: %s%n", user.getUsername(), user.getPassword());
    }

    public void removeUser(String userId) throws UserDoesNotExist_Exception {
        UserManager userManager = UserManager.getInstance();
        userManager.remove(userId);
    }

    public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception {
        UserManager userManager = UserManager.getInstance();
        userManager.authenticate(userId, reserved);
        return new byte[] { 1 };
    }
}
