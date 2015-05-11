package pt.tecnico.bubbledocs.service.local;

import java.util.Random;

import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Session;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;

public class LoginUser extends BubbleDocsService {

    private String token;
    private String key;
    private String ticket;
    private String username;
    private String password;

    public LoginUser(String username, String password) {
        this(username, password, null, null);
    }

    public LoginUser(String username, String password, String key, String ticket) {
        super();
        this.key = key;
        this.username = username;
        this.password = password;
        this.ticket = ticket;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        if (getUsername() == null)
            throw new LoginBubbleDocsException();

        if (getPassword() == null)
            throw new LoginBubbleDocsException();

        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        sm.cleanOldSessions();
        User user = bd.getUserByUsername(getUsername());

        if (user == null)
            throw new LoginBubbleDocsException();

        if (user.getPassword() == null || !user.getPassword().equals(getPassword()))
            throw new LoginBubbleDocsException();

        String prevToken = "";

        if (user.getSession() != null && user.getSession().getToken() != null) {
            prevToken = user.getSession().getToken();
        }

        do {
            token = username + new Random().nextInt(10);
        } while (token.equals(prevToken));

        sm.addSession(new Session(getBubbleDocs().getUserByUsername(getUsername()), getUserToken(), new DateTime(), key, ticket));

    }

    public final String getUserToken() {
        return token;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public final String getKey() {
        return key;
    }

    public final String getTicket() {
        return ticket;
    }

}
