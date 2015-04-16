package pt.ulisboa.tecnico.sdis.id.ws.impl;

import java.lang.reflect.Field;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdImplTest {

    private final String USERNAME = "user";
    private final String EMAIL = "abc@def.com";
    private final String WRONG_PASSWORD = "wrong";
    private final String ANOTHER_USER = "anotherUser";
    private final String ANOTHER_EMAIL = "another@email.com";

    private IdImpl id;
    private UserManager manager;

    @Before
    public void setUp() {
        id = new IdImpl();
        manager = UserManager.getInstance();
    }

    @After
    public void tearDown() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        id = null;
        manager = null;
        Field field = UserManager.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void testSuccessfullCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
    }

    @Test(expected = InvalidUser_Exception.class)
    public void testEmptyUsernameCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        id.createUser("", EMAIL);
    }

    @Test(expected = InvalidUser_Exception.class)
    public void testNullUsernameCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        id.createUser(null, EMAIL);
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testEmptyEmailCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        id.createUser(USERNAME, "");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testNullEmailCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        id.createUser(USERNAME, null);
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_1() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "use$r1@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_2() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1@exa$mple.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_3() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1@example.co$m");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_4() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_5() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_6() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1@.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_7() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1@com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_8() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "user1@@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void testInvalidEmailCreateUser_9() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, "sexy_bóy_69@users.brazzers.com");
    }

    @Test(expected = UserAlreadyExists_Exception.class)
    public void testDuplicatedUsernameCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.createUser(USERNAME, ANOTHER_EMAIL);
    }

    @Test(expected = EmailAlreadyExists_Exception.class)
    public void testDuplicatedEmailCreateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.createUser(ANOTHER_USER, EMAIL);
    }

    @Test
    public void testSuccessfullRenewPassword() throws UserDoesNotExist_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.renewPassword(USERNAME);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testUnexistingUserRenewPassword() throws UserDoesNotExist_Exception {
        id.renewPassword(USERNAME);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testNullUserRenewPassword() throws UserDoesNotExist_Exception {
        id.renewPassword(null);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testEmptyUserRenewPassword() throws UserDoesNotExist_Exception {
        id.renewPassword("");
    }

    @Test
    public void testSuccessfullRemoveUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        id.createUser(USERNAME, EMAIL);
        id.removeUser(USERNAME);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testUnexistingUserRemoveUser() throws UserDoesNotExist_Exception {
        id.removeUser(USERNAME);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testEmptyUserRemoveUser() throws UserDoesNotExist_Exception {
        id.removeUser("");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void testNullUserRemoveUser() throws UserDoesNotExist_Exception {
        id.removeUser(null);
    }

    @Test
    public void testSuccessfullRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        String password = manager.create(USERNAME, EMAIL).getPassword();
        id.requestAuthentication(USERNAME, password.getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testWrongPasswordRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.requestAuthentication(USERNAME, WRONG_PASSWORD.getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testEmptyPasswordRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.requestAuthentication(USERNAME, "".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testNullPasswordRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        id.createUser(USERNAME, EMAIL);
        id.requestAuthentication(USERNAME, null);
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testWrongUsernameRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        String password = manager.create(USERNAME, EMAIL).getPassword();
        id.requestAuthentication(ANOTHER_USER, password.getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testEmptyUsernameRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        String password = manager.create(USERNAME, EMAIL).getPassword();
        id.requestAuthentication("", password.getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void testNullUsernameRequestAuthentication() throws AuthReqFailed_Exception, EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        String password = manager.create(USERNAME, EMAIL).getPassword();
        id.requestAuthentication(null, password.getBytes());
    }

}
