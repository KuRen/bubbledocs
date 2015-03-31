package pt.tecnico.bubbledocs.domain;

import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class SessionManager extends SessionManager_Base {

    // In milliseconds
    private static final int EXPIRATION_TIME = 2 * 60 * 60 * 1000;

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
        for (Session session : this.getSessionSet()) {
            if (session.getToken().equals(token)) {
                if (isExpired(session))
                    throw new TokenExpiredException();

                return session.getUser();
            }
        }
        return null;
    }

    public void refreshSession(String token) {
        for (Session session : this.getSessionSet()) {
            if (session.getToken().equals(token)) {
                session.setLastActivity(new DateTime());
                return;
            }
        }
        throw new UserNotInSessionException();
    }

    private boolean isExpired(Session session) {
        DateTime nowDateTime = new DateTime();
        return (nowDateTime.getMillis() - session.getLastActivity().getMillis()) >= EXPIRATION_TIME;
    }
}
