package pt.ulisboa.tecnico.sdis.id.ws.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

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

public class UserManagerTest {

    UserManager userManager;

    @Before
    public void setUp() {
        userManager = UserManager.getInstance();
    }

    @After
    public void tearDown() throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException,
            IllegalAccessException {
        userManager = null;
        Field field = UserManager.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void isSingleton() {
        assertSame(userManager, UserManager.getInstance());
    }

    @Test
    public void isInitiallyEmpty() {
        assertEquals(0, userManager.size());
    }

    @Test
    public void createUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        User user = userManager.create("user", "email@example.com");

        assertEquals("user", user.getUsername());
        assertEquals("email@example.com", user.getEmail());
        assertNotNull(user.getPassword());

        assertEquals(1, userManager.size());
    }

    @Test
    public void createUserWithPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        User user = userManager.create("user", "email@example.com", "password");

        assertEquals("user", user.getUsername());
        assertEquals("email@example.com", user.getEmail());
        assertEquals("password", user.getPassword());

        assertEquals(1, userManager.size());
    }

    @Test
    public void createMultipleUsers() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@example.com");
        userManager.create("user2", "user2@example.com");

        assertEquals(2, userManager.size());

        userManager.create("user3", "user3@example.com");

        assertEquals(3, userManager.size());
    }

    @Test(expected = EmailAlreadyExists_Exception.class)
    public void duplicateEmail() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@example.com");
        userManager.create("user2", "user1@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_0() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", null);
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_1() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "use$r1@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_2() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@exa$mple.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_3() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@example.co$m");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_4() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_5() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_6() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_7() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_8() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@@example.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_9() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "sexy_bóy_69@users.brazzers.com");
    }

    @Test(expected = InvalidEmail_Exception.class)
    public void invalidEmail_10() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "");
    }

    @Test
    public void validEmails() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "a@b.c");
        userManager.create("user2", "a_b@b.c");
        userManager.create("user3", "a_b.c@b.c");
        userManager.create("user4", "a@b.c.d");
        userManager.create("user5", "a_b.c@b.c.d.e");
        userManager.create("user6", "aaa_bbb.ccc@aaa.bbb.ccc.ddd.eee");
    }

    @Test(expected = UserAlreadyExists_Exception.class)
    public void duplicateUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("user1", "user1@example.com");
        userManager.create("user1", "user2@example.com");
    }

    @Test(expected = InvalidUser_Exception.class)
    public void invalidUser_0() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create("", "user1@example.com");
    }

    @Test(expected = InvalidUser_Exception.class)
    public void invalidUser_1() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        userManager.create(null, "user1@example.com");
    }

    @Test
    public void renewToRandomPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        User user = userManager.create("user", "email@example.com", "password");
        userManager.renewPassword("user");

        assertNotNull(user.getPassword());
        assertFalse(user.getPassword().equals("password"));
    }

    @Test
    public void renewToSpecificPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        User user = userManager.create("user", "email@example.com");
        userManager.renewPassword("user", "newpw");

        assertNotNull(user.getPassword());
        assertEquals("newpw", user.getPassword());
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewNullUserPassword() throws UserDoesNotExist_Exception {
        userManager.renewPassword(null);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewEmtpyUserPassword() throws UserDoesNotExist_Exception {
        userManager.renewPassword("");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void renewNonExistingUserPassword() throws UserDoesNotExist_Exception {
        userManager.renewPassword("user99");
    }

    @Test
    public void removeUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, UserDoesNotExist_Exception {
        userManager.create("user", "email@example.com", "password");
        userManager.remove("user");

        assertEquals(0, userManager.size());
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeNullUser() throws UserDoesNotExist_Exception {
        userManager.remove(null);
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeEmptyUser() throws UserDoesNotExist_Exception {
        userManager.remove("");
    }

    @Test(expected = UserDoesNotExist_Exception.class)
    public void removeNonExistingUser() throws UserDoesNotExist_Exception {
        userManager.remove("user99");
    }

    @Test
    public void authenticate() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("user", "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNullUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate(null, "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateEmptyUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("", "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNonExistingUser() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("user99", "pw".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateNullPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("user", null);
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateEmptyPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("user99", "".getBytes());
    }

    @Test(expected = AuthReqFailed_Exception.class)
    public void authenticateWrongPassword() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception, AuthReqFailed_Exception {
        userManager.create("user", "email@example.com", "pw");
        userManager.authenticate("user99", "notpw".getBytes());
    }

}
