package pt.tecnico.bubbledocs.domain;

import org.joda.time.Hours;
import org.joda.time.LocalTime;

import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class SessionManager extends SessionManager_Base {
    
    public SessionManager() {
        super();
    }
    
    public void cleanOldSessions() {
    	for(Session session : this.getSessionSet()) {
    		if(Hours.hoursBetween(session.getLastActivity(), new LocalTime()).getHours() >= 2) {
    			this.removeSession(session);
    			session.delete();
    		}
    	}
    }
    
    public boolean userIsInSession(String username) {
    	for(Session session : this.getSessionSet()) {
    		if(session.getUsername().equals(username)) return true;
    	}
    	return false;
    }
    
    public String findUserByToken(String token) {
    	for(Session session : this.getSessionSet()) {
    		if(session.getToken().equals(token)) return session.getUsername();
    	}
    	return null;
    }
    
    public void refreshSession(String username) {
    	for(Session session : this.getSessionSet()) {
    		if(session.getUsername().equals(username)) {
    			session.setLastActivity(new LocalTime());
    			return;
    		}
    	}
    	throw new UserNotInSessionException();
    }
    
}
