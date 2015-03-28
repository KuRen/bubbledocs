package pt.tecnico.bubbledocs.domain;

import org.joda.time.LocalTime;

public class Session extends Session_Base {
    
    public Session() {
        super();
    }
    
    public Session(String username, String token, LocalTime lastActivity) {
        super();
        setUsername(username);
        setToken(token);
        setLastActivity(lastActivity);
    }
    
    public void delete() {
    	this.setManager(null);
    	this.deleteDomainObject();
    }
    
}
