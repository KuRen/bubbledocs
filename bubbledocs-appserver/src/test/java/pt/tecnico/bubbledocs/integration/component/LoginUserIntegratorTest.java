package pt.tecnico.bubbledocs.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.integration.LoginUserIntegrator;
import pt.tecnico.bubbledocs.service.dto.AuthenticationResult;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUserIntegratorTest extends BubbleDocsIntegratorTest {
    private static final String USERNAME = "jpierre";
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
    /*private DateTime getLastAccessTimeInSession(String userToken) {
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        for (Session session : sm.getSessionSet()) {
            if (session.getToken().equals(userToken))
                return session.getLastActivity();
        }
        return null;
    }*/

    @Test
    public void success() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new AuthenticationResult("key", "ticket");
            }
        };

        service.execute();

        User user = getUserFromSession(service.getUserToken());
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void successLoginTwice() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new AuthenticationResult("key", "ticket");
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new AuthenticationResult("key", "ticket");
            }
        };

        service.execute();
        String token1 = service.getUserToken();

        service.execute();
        String token2 = service.getUserToken();

        assertFalse(token1.equals(token2));
        User user = getUserFromSession(token1);
        assertNull(user);
        user = getUserFromSession(token2);
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void successLoginMultipleTimes() {
        final int N = 15;

        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new AuthenticationResult("key", "ticket");
                times = N;
            }
        };

        String token = null;
        String prevToken = null;

        for (int i = 0; i < N; i++) {
            service.execute();
            token = service.getUserToken();
            assertFalse(token.equals(prevToken));
        }
    }

    @Test
    public void refreshLocalPassword() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, DIFF_FROM_LOCAL_PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, DIFF_FROM_LOCAL_PASSWORD);
                result = new AuthenticationResult("key", "ticket");
            }
        };

        service.execute();

        System.out.println(user.getPassword());
        System.out.println(DIFF_FROM_LOCAL_PASSWORD);

        assertEquals(DIFF_FROM_LOCAL_PASSWORD, user.getPassword());
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void nullUser() {
        LoginUserIntegrator service = new LoginUserIntegrator(null, PASSWORD);
        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void nullPasswordr() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, null);
        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void remoteLoginUnknownUser() {
        LoginUserIntegrator service = new LoginUserIntegrator(NON_EXISTING_USERNAME, PASSWORD);

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
        LoginUserIntegrator service = new LoginUserIntegrator(NON_EXISTING_USERNAME, PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(NON_EXISTING_USERNAME, PASSWORD);
                result = new RemoteInvocationException();
            }
        };

        service.execute();
    }

    @Test(expected = LoginBubbleDocsException.class)
    public void remoteLoginUserIntegratorWithWrongPassword() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, WRONG_PASSWORD);

        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, WRONG_PASSWORD);
                result = new LoginBubbleDocsException();
            }
        };

        service.execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void localLoginUserIntegratorWithWrongPassword() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, WRONG_PASSWORD);
        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, WRONG_PASSWORD);
                result = new RemoteInvocationException();
            }
        };
        service.execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void localLoginUserIntegratorWithVoidedPassword() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);
        user.setPassword(null);
        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new RemoteInvocationException();
            }
        };
        service.execute();
    }

    @Test
    public void localLoginUserIntegratorSuccess() {
        LoginUserIntegrator service = new LoginUserIntegrator(USERNAME, PASSWORD);
        new Expectations() {
            {
                idRemoteServices.loginUser(USERNAME, PASSWORD);
                result = new RemoteInvocationException();
            }
        };
        service.execute();

        User user = getUserFromSession(service.getUserToken());
        assertEquals(USERNAME, user.getUsername());
    }
}
