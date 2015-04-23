package pt.tecnico.bubbledocs.service.local;

import java.util.Random;

import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Session;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUser extends BubbleDocsService {
    private String userToken;
    private String username;
    private String password;
    private IDRemoteServices idRemoteServices;

    public LoginUser(String username, String password) {
        this.username = username;
        this.password = password;
        idRemoteServices = new IDRemoteServices();
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

        try {
            idRemoteServices.loginUser(username, password);
            if (user.getPassword() == null || !user.getPassword().equals(password))
                user.setPassword(password);
        } catch (RemoteInvocationException e) {
            if (user == null)
                throw new UnavailableServiceException();
            if (user.getPassword() == null)
                throw new UnavailableServiceException();
            if (user.getPassword().equals(getPassword())) {
                if (sm.userIsInSession(getUsername())) {
                    for (Session session : sm.getSessionSet()) {
                        if (session.getUsername().equals(getUsername())) {
                            sm.removeSession(session);
                            session.delete();
                        }
                    }
                }

            } else
                throw new UnavailableServiceException();
        }

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