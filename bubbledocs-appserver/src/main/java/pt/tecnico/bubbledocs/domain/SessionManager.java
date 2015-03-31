package pt.tecnico.bubbledocs.domain;

import java.util.Random;

import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class SessionManager extends SessionManager_Base {

    // In milliseconds
    public static final int EXPIRATION_TIME = 2 * 60 * 60 * 1000;

    public SessionManager() {
        super();
    }

    public void cleanOldSessions() {
        for (Session session : this.getSessionSet()) {
            if (isExpired(session)) {
                this.removeSession(session);
                session.delete();
            }
        }
    }

    public boolean userIsInSession(String username) {
        for (Session session : this.getSessionSet()) {
            if (session.getUsername().equals(username))
                return true;
        }
        return false;
    }

    public User findUserByToken(String token) {
        Session session = findSessionByToken(token);
        if (session == null)
            return null;
        if (isExpired(session))
            throw new TokenExpiredException();
        return session.getUser();
    }

    public void refreshSession(String token) {
        Session session = findSessionByToken(token);
        if (session == null)
            throw new UserNotInSessionException();
        session.refresh();
    }

    public Session findSessionByToken(String token) {
        for (Session session : this.getSessionSet()) {
            if (session.getToken().equals(token)) {
                return session;
            }
        }
        return null;
    }

    private boolean isExpired(Session session) {
        DateTime nowDateTime = new DateTime();
        return (nowDateTime.getMillis() - session.getLastActivity().getMillis()) >= EXPIRATION_TIME;
    }

    public String addUserToSession(User user) {
        String token = user.getUsername() + new Random().nextInt(10);
        Session session = new Session(user, token, new DateTime());
        addSession(session);
        return token;
    }
}
