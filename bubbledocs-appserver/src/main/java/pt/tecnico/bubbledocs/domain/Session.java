package pt.tecnico.bubbledocs.domain;

import org.joda.time.DateTime;

public class Session extends Session_Base {

    public Session() {
        super();
    }

    public Session(User user, String token, DateTime lastActivity) {
        super();
        setUser(user);
        setToken(token);
        setLastActivity(lastActivity);
    }

    public void delete() {
        this.setManager(null);
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
