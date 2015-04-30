package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class GetUsernameForTokenService extends BubbleDocsService {

    private String token;
    private String username;

    public GetUsernameForTokenService(String token) {
        this.token = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bubbleDocs = getBubbleDocs();
        SessionManager sessionManager = bubbleDocs.getSessionManager();

        sessionManager.cleanOldSessions();

        User user = sessionManager.findUserByToken(token);

        this.username = user.getUsername();
    }

    public String getUsername() {
        return username;
    }

}
