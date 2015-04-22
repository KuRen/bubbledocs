package pt.tecnico.bubbledocs.integration;

import java.util.Random;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Session;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;

public class BubbleDocsIntegrationTest {

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
            populate4Test();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    public void populate4Test() {

    }

    User createUser(String username, String password, String email, String name) {
        BubbleDocs bd = BubbleDocs.getInstance();
        User user = new User(username, password, email, name);
        bd.addUsers(user);
        return user;
    }

    String addUserToSession(String username) {
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        String token = username + new Random().nextInt(10);
        User user = getUserFromUsername(username);
        sm.addSession(new Session(user, token, new DateTime()));
        return token;
    }

    User getUserFromUsername(String username) {
        return getBubbleDocs().getUserByUsername(username);
    }

    BubbleDocs getBubbleDocs() {
        return BubbleDocs.getInstance();
    }

    User getUserFromSession(String token) {
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        return sm.findUserByToken(token);
    }

    boolean expireToken(String token) {
        // very very ugly. but there is no relationship between User and Session
        SessionManager sessionManager = FenixFramework.getDomainRoot().getBubbleDocs().getSessionManager();
        Session session = null;
        for (Session s : sessionManager.getSessionSet()) {
            if (s.getToken().equals(token)) {
                session = s;
                break;
            }
        }

        if (session == null)
            return false;

        DateTime lastActivity = session.getLastActivity();
        session.setLastActivity(lastActivity.minusMillis(SessionManager.EXPIRATION_TIME * 2));

        return true;
    }
}
