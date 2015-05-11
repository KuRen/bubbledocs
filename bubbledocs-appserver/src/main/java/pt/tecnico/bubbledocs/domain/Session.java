package pt.tecnico.bubbledocs.domain;

import org.joda.time.DateTime;

public class Session extends Session_Base {

    public Session() {
        super();
    }

    public Session(User user, String token, DateTime lastActivity) {
        this(user, token, lastActivity, null, null);
    }

    public Session(User user, String token, DateTime lastActivity, String key, String ticket) {
        super();
        setUser(user);
        setToken(token);
        setLastActivity(lastActivity);
        setKey(key);
        setTicket(ticket);
    }

    public void delete() {
        this.setSessionManager(null);
        this.setUser(null);
        this.deleteDomainObject();
    }

    public String getUsername() {
        return this.getUser().getUsername();
    }

    public void refresh() {
        setLastActivity(new DateTime());
    }

}
