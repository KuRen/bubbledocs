package pt.tecnico.bubbledocs.service.remote;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

@Ignore("Tests done in the SD Client and BubbleDocs AppServer System Testing")
public class MockedIdRemoteServicesTest extends SdRemoteServicesTest {

    IDRemoteServices service = null;

    @Mocked
    private SDId remotePort;

    @Mocked
    private SDRemoteServices remoteServices;

    @Override
    @Before
    public void setUp() {
        service = new IDRemoteServices();
    }

    @Override
    @After
    public void tearDown() {
        service = null;
    }

    @Test
    public void successCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
            }
        };

        service.createUser("user", "user@example.com");
    }

    @Test
    public void createMultipleUsers() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
                remotePort.createUser("user2", "user2@example.com");
                remotePort.createUser("user3", "user3@example.com");
            }
        };
        service.createUser("user", "user@example.com");
        service.createUser("user2", "user2@example.com");
        service.createUser("user3", "user3@example.com");
    }

    @Test(expected = DuplicateEmailException.class)
    public void duplicateEmail() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
                remotePort.createUser("user2", "user@example.com");
                result = new EmailAlreadyExists_Exception(anyString, null);
            }
        };
        service.createUser("user", "user@example.com");
        service.createUser("user2", "user@example.com");
    }

    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
                result = new InvalidEmail_Exception(anyString, null);
            }
        };
        service.createUser("user", null);
    }

    @Test(expected = DuplicateUsernameException.class)
    public void duplicateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
                remotePort.createUser("user2", "user2@example.com");
                result = new UserAlreadyExists_Exception(anyString, null);
            }
        };

        service.createUser("user", "user@example.com");
        service.createUser("user", "user2@example.com");
    }

    @Test(expected = InvalidUsernameException.class)
    public void invalidUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "user@example.com");
                result = new InvalidUser_Exception(anyString, null);
            }
        };
        service.createUser("", "user@example.com");
    }

    @Test
    public void renewToRandomPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.renewPassword("user");
            }
        };
        service.createUser("user", "email@example.com");
        service.renewPassword("user");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewNullUserPassword() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.renewPassword(null);
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.renewPassword(null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewEmtpyUserPassword() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.renewPassword("");
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.renewPassword("");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void renewNonExistingUserPassword() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.renewPassword("user99");
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.renewPassword("user99");
    }

    @Test
    public void removeUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.removeUser("user");
            }
        };
        service.createUser("user", "email@example.com");
        service.removeUser("user");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeNullUser() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.removeUser(null);
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.removeUser(null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeEmptyUser() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.removeUser("");
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.removeUser("");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void removeNonExistingUser() throws UserDoesNotExist_Exception {
        new Expectations() {
            {
                remotePort.removeUser("user99");
                result = new UserDoesNotExist_Exception(anyString, null);
            }
        };
        service.removeUser("user99");
    }

    @Test
    public void authenticate() throws AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.requestAuthentication("alice", "Aaa1".getBytes());
            }
        };
        service.loginUser("alice", "Aaa1");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNullUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        service.createUser("user", "email@example.com");
        service.loginUser(null, "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateEmptyUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.requestAuthentication("", "pw".getBytes());
                result = new AuthReqFailed_Exception(anyString, null);
            }
        };
        service.createUser("user", "email@example.com");
        service.loginUser("", "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNonExistingUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.requestAuthentication("user99", "pw".getBytes());
                result = new AuthReqFailed_Exception(anyString, null);
            }
        };
        service.createUser("user", "email@example.com");
        service.loginUser("user99", "pw");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateNullPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
            }
        };
        service.createUser("user", "email@example.com");
        service.loginUser("user", null);
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateEmptyPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.requestAuthentication("", "".getBytes());
                result = new AuthReqFailed_Exception(anyString, null);
            }
        };
        service.createUser("user", "email@example.com");
        service.loginUser("user", "");
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void authenticateWrongPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        new Expectations() {
            {
                remotePort.createUser("user", "email@example.com");
                remotePort.requestAuthentication("", "notpw".getBytes());
                result = new AuthReqFailed_Exception(anyString, null);
            }
        };
        service.createUser("user", "email@example.com");
        service.loginUser("user", "notpw");
    }

}
