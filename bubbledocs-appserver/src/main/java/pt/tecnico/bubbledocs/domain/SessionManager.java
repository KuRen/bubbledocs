package pt.tecnico.bubbledocs.domain;

import org.joda.time.Hours;
import org.joda.time.LocalTime;

import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class SessionManager extends SessionManager_Base {

    private static final int TIMEDIFFERENCE = 2;

    public SessionManager() {
        super();
    }

    public void cleanOldSessions() {
        for (Session session : this.getSessionSet()) {
            if (Hours.hoursBetween(session.getLastActivity(), new LocalTime()).getHours() >= TIMEDIFFERENCE) {
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
                if (Hours.hoursBetween(session.getLastActivity(), new LocalTime()).getHours() < TIMEDIFFERENCE) {
                    return session.getUser();
                } else {
                    throw new TokenExpiredException();
                }
            }
        }
        return null;
    }

    public void refreshSession(String token) {
        for (Session session : this.getSessionSet()) {
            if (session.getToken().equals(token)) {
                session.setLastActivity(new LocalTime());
                return;
            }
        }
        throw new UserNotInSessionException();
    }

}
