package pt.tecnico.bubbledocs.service.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;

public class LoginUserTest extends BubbleDocsServiceTest {

    private static final String USERNAME = "jpierre";
    private static final String NON_EXISTING_USERNAME = "otherone";
    private static final String PASSWORD = "jp#";
    private static final String EMAIL = "joao.pereira@tecnico.ulisboa.pt";

    User user;

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
        LoginUser service = new LoginUser(USERNAME, PASSWORD);

        service.execute();

        User user = getUserFromSession(service.getUserToken());
        assertEquals(USERNAME, user.getUsername());
    }

    @Test
    public void successLoginTwice() {
        LoginUser service = new LoginUser(USERNAME, PASSWORD);

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

        LoginUser service = new LoginUser(USERNAME, PASSWORD);

        String token = null;
        String prevToken = null;

        for (int i = 0; i < N; i++) {
            service.execute();
            token = service.getUserToken();
            assertFalse(token.equals(prevToken));
        }
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
        service.execute();
    }
}
