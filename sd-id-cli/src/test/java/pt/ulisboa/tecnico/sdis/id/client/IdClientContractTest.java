package pt.ulisboa.tecnico.sdis.id.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClientContractTest {
    private final String uddiURL = "http://localhost:8081";
    private final String serviceName = "SD-ID";
    private IdClient client = null;

    @Before
    public void setUp() throws ServiceLookupException {
        client = new IdClient(uddiURL, serviceName);
        //client.lookForService(uddiURL, serviceName);
        //client.createStub();
    }

    @After
    public void tearDown() {
        try {
            client.removeUser("user");
        } catch (UserDoesNotExist_Exception e) {
        }

        client = null;
    }

    @Test
    public void successCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {

        client.createUser("user", "user@example.com");
    }

    @Test
    public void createMultipleUsers() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        try {
            client.createUser("user", "user@example.com");
            client.createUser("user2", "user2@example.com");
            client.createUser("user3", "user3@example.com");
        } finally {
            client.removeUser("user2");
            client.removeUser("user3");
        }

    }

    @Test(expected = EmailAlreadyExists_Exception.class)
    public void duplicateEmail() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        client.createUser("user", "user@example.com");
        client.createUser("user2", "user@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_0() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", null);
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_1() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "use$r1@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_2() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@exa$mple.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_3() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@example.co$m");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_4() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "userexample.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_5() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_6() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_7() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_8() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_9() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "sexy_bóy_69@users.brazzers.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_10() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "");
    }

    @Test
    public void validEmails() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        try {
            client.createUser("user", "a@b.c");
            client.createUser("user2", "a_b@b.c");
            client.createUser("user3", "a_b.c@b.c");
            client.createUser("user4", "a@b.c.d");
            client.createUser("user5", "a_b.c@b.c.d.e");
            client.createUser("user6", "aaa_bbb.ccc@aaa.bbb.ccc.ddd.eee");
        } finally {
            client.removeUser("user2");
            client.removeUser("user3");
            client.removeUser("user4");
            client.removeUser("user5");
            client.removeUser("user6");
        }

    }

    @Test(expected = UserAlreadyExists_Exception.class)
    public void duplicateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("user", "user@example.com");
        client.createUser("user", "user2@example.com");
    }

    @Test(expected = InvalidUser_Exception.class)
    public void invalidUser_0() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser("", "user@example.com");
    }

    @Test(expected = InvalidUser_Exception.class)
    public void invalidUser_1() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        client.createUser(null, "user@example.com");
    }

    @Test
    public void renewToRandomPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        client.createUser("user", "email@example.com");
        client.renewPassword("user");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewNullUserPassword() throws UserDoesNotExist_Exception {
        client.renewPassword(null);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewEmtpyUserPassword() throws UserDoesNotExist_Exception {
        client.renewPassword("");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewNonExistingUserPassword() throws UserDoesNotExist_Exception {
        client.renewPassword("user99");
    }

    @Test
    public void removeUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        client.createUser("user", "email@example.com");
        client.removeUser("user");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeNullUser() throws UserDoesNotExist_Exception {
        client.removeUser(null);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeEmptyUser() throws UserDoesNotExist_Exception {
        client.removeUser("");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeNonExistingUser() throws UserDoesNotExist_Exception {
        client.removeUser("user99");
    }

    @Test
    public void authenticate() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.requestAuthentication("alice", "Aaa1".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNullUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication(null, "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateEmptyUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication("", "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNonExistingUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication("user99", "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNullPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication("user", null);
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateEmptyPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication("user", "".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateWrongPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        client.createUser("user", "email@example.com");
        client.requestAuthentication("user", "notpw".getBytes());
    }

}
