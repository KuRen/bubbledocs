package pt.tecnico.bubbledocs.service;

import java.util.Random;

import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Session;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.UnknownBubbleDocsUserException;
import pt.tecnico.bubbledocs.exception.WrongPasswordException;

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
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getManager();
        sm.cleanOldSessions();
        User user = bd.getUserByUsername(getUsername());

        if (user == null)
            throw new UnknownBubbleDocsUserException();

        if (user.getPassword().equals(getPassword())) {
            if (sm.userIsInSession(getUsername())) {
                for (Session session : sm.getSessionSet()) {
                    if (session.getUsername().equals(getUsername())) {
                        sm.removeSession(session);
                        session.delete();
                    }
                }
            }
            userToken = username + new Random().nextInt(10);
            sm.addSession(new Session(getBubbleDocs().getUserByUsername(getUsername()), getUserToken(), new DateTime()));
        } else
            throw new WrongPasswordException();
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
