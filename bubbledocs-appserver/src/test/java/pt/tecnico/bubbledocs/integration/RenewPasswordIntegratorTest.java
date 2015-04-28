package pt.tecnico.bubbledocs.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPasswordIntegratorTest extends BubbleDocsIntegrationTest {
    private final String NAME = "the name";
    private final String EMAIL = "email@example.com";
    private final String USERNAME = "user25";
    private final String PASSWORD = "thepassword";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";
    private String USER_TOKEN;

    @Mocked
    IDRemoteServices idRemoteServices;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, NAME);
        USER_TOKEN = addUserToSession(USERNAME);
    }

    @Test
    public void success() {
        RenewPasswordIntegrator service = new RenewPasswordIntegrator(USER_TOKEN);

        new Expectations() {
            {
                idRemoteServices.renewPassword(USERNAME);
            }
        };

        service.execute();

        User user = getUserFromSession(USER_TOKEN);
        assertNull(user.getPassword());
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new RenewPasswordIntegrator(null).execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new RenewPasswordIntegrator("").execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new RenewPasswordIntegrator(NOT_IN_SESSION_TOKEN).execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(USER_TOKEN);
        new RenewPasswordIntegrator(USER_TOKEN).execute();
    }

    @Test
    public void remoteException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.renewPassword(USERNAME);
                result = new RemoteInvocationException();
            }
        };
        try {
            new RenewPasswordIntegrator(USER_TOKEN).execute();
            fail("Expected UnavailableServiceException");
        } catch (UnavailableServiceException use) {
            assertEquals(PASSWORD, getUserFromSession(USER_TOKEN).getPassword());
        }
    }

    @Test
    public void loginBubbleDocsException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.renewPassword(USERNAME);
                result = new LoginBubbleDocsException();
            }
        };
        try {
            new RenewPasswordIntegrator(USER_TOKEN).execute();
            fail("Expected UnavailableServiceException");
        } catch (LoginBubbleDocsException lbe) {
            assertEquals(PASSWORD, getUserFromSession(USER_TOKEN).getPassword());
        }
    }
}
