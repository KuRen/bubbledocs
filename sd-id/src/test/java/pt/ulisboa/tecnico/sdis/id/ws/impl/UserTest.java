package pt.ulisboa.tecnico.sdis.id.ws.impl;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String EMAIL = "email@example.com";
    private final String NEW_USERNAME = "uname";
    private final String NEW_PASSWORD = "psswd";
    private final String NEW_EMAIL = "john_doe@example.com";

    private User user;

    @Before
    public void setUp() {
        user = new User(USERNAME, PASSWORD, EMAIL);
    }

    @After
    public void tearDown() {
        user = null;
    }

    @Test
    public void getUsername() {
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void getPassword() {
        assertEquals(PASSWORD, user.getPassword());
    }

    @Test
    public void getEmail() {
        assertEquals(EMAIL, user.getEmail());
    }

    @Test
    public void setUsername() {
        user.setUsername(NEW_USERNAME);
        assertEquals(NEW_USERNAME, user.getUsername());
    }

    @Test
    public void setPassword() {
        user.setPassword(NEW_PASSWORD);
        assertEquals(NEW_PASSWORD, user.getPassword());
    }

    @Test
    public void setEmail() {
        user.setEmail(NEW_EMAIL);
        assertEquals(NEW_EMAIL, user.getEmail());
    }

}
