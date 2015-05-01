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
    
    private String userToken;
    private String username;
    private String password;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
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
        
        if(user == null) throw new LoginBubbleDocsException();
        
        if (user.getPassword() == null || !user.getPassword().equals(getPassword()))
            throw new LoginBubbleDocsException();

        String prevToken = "";

        if (user.getSession() != null && user.getSession().getToken() != null) {
            prevToken = user.getSession().getToken();
        }

        do {
            userToken = username + new Random().nextInt(10);
        } while (userToken.equals(prevToken));

        sm.addSession(new Session(getBubbleDocs().getUserByUsername(getUsername()), getUserToken(), new DateTime()));

    }

    public final String getUserToken() {
        return userToken;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

}
