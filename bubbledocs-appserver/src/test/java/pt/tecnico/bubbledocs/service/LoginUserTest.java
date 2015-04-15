package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Expectations;
import mockit.Mocked;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Session;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUserTest extends BubbleDocsServiceTest {

    private static final String USERNAME = "jp";
    private static final String NON_EXISTING_USERNAME = "otherone";
    private static final String PASSWORD = "jp#";
    private static final String WRONG_PASSWORD = "wrongpsswd";
    private static final String DIFF_FROM_LOCAL_PASSWORD = "diffFromLocal";
    private static final String EMAIL = "joao.pereira@tecnico.ulisboa.pt";

    User user;

    @Mocked
    IDRemoteServices idRemoteServices;

    @Override
    public void populate4Test() {
        user = createUser(USERNAME, PASSWORD, EMAIL, "João Pereira");
    }

    // returns the time of the last access for the user with token userToken.
    // It must get this data from the session object of the application
    private DateTime getLastAccessTimeInSession(String userToken) {
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        for (Session session : sm.getSessionSet()) {
            if (session.getToken().equals(userToken))
                return session.getLastActivity();
        }
        return null;
    }

    @Test
    public void success() {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
            }
        };

        service.execute();
        DateTime currentTime = new DateTime();

        String token = service.getUserToken();

        User user = getUserFromSession(service.getUserToken());
        assertEquals(USERNAME, user.getUsername());

        int difference = Seconds.secondsBetween(getLastAccessTimeInSession(token), currentTime).getSeconds();

        assertTrue("Access time in session not correctly set", difference >= 0);
        assertTrue("diference in seconds greater than expected", difference < 2);
    }

    @Test
    public void successLoginTwice() {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                idRemoteServices.loginUser(USERNAME, PASSWORD);
            }
        };

        service.execute();
        String token1 = service.getUserToken();

        service.execute();
        String token2 = service.getUserToken();

        User user = getUserFromSession(token1);
        assertNull(user);
        user = getUserFromSession(token2);
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void refreshLocalPassword() {
        LoginUser service = new LoginUser(USERNAME, DIFF_FROM_LOCAL_PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, DIFF_FROM_LOCAL_PASSWORD);
            }
        };

        service.execute();

        assertEquals(DIFF_FROM_LOCAL_PASSWORD, user.getPassword());
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void nullUser() {
        LoginUser service = new LoginUser(null, PASSWORD);
        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void nullPasswordr() {
        LoginUser service = new LoginUser(USERNAME, null);
        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void remoteLoginUnknownUser() {
        LoginUser service = new LoginUser(NON_EXISTING_USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(NON_EXISTING_USERNAME, PASSWORD);
                result = new LoginBubbleDocsException();
            }
        };

        service.execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void localLoginUnknownUser() {
        LoginUser service = new LoginUser(NON_EXISTING_USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(NON_EXISTING_USERNAME, PASSWORD);
                result = new RemoteInvocationException();
            }
        };

        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void remoteLoginUserWithWrongPassword() {
        LoginUser service = new LoginUser(USERNAME, WRONG_PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, WRONG_PASSWORD);
                result = new LoginBubbleDocsException();
            }
        };

        service.execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void localLoginUserWithWrongPassword() {
        LoginUser service = new LoginUser(USERNAME, WRONG_PASSWORD);
        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, WRONG_PASSWORD);
                result = new RemoteInvocationException();
            }
        };
        service.execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void localLoginUserWithVoidedPassword() {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);
        user.setPassword(null);
        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new RemoteInvocationException();
            }
        };
        service.execute();
    }
}
