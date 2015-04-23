package pt.tecnico.bubbledocs.service.local;

import jvstm.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public abstract class BubbleDocsService {

    @Atomic
    public final void execute() throws BubbleDocsException {
        dispatch();
    }

    protected static BubbleDocs getBubbleDocs() {
        return FenixFramework.getDomainRoot().getBubbleDocs();
    }

    protected abstract void dispatch() throws BubbleDocsException;

    protected void refreshToken(String token) {
        SessionManager sessionManager = getBubbleDocs().getSessionManager();
        sessionManager.refreshSession(token);
    }

    protected User getLoggedInUser(String token) {
        if (token == null || token.isEmpty()) {
            throw new InvalidArgumentException();
        }

        SessionManager sm = getBubbleDocs().getSessionManager();
        User user = sm.findUserByToken(token);

        if (user == null)
            throw new UserNotInSessionException();

        return user;
    }
}