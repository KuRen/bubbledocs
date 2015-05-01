package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;

public class DeleteUser extends BubbleDocsService {

    private String userToken;
    private String toDeleteUsername;

    public DeleteUser(String userToken, String toDeleteUsername) {
        this.userToken = userToken;
        this.toDeleteUsername = toDeleteUsername;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bd = BubbleDocs.getInstance();
        SessionManager sm = bd.getSessionManager();
        sm.cleanOldSessions();

        User user = getLoggedInUser(userToken);

        if (!user.isRoot()) {
            throw new UnauthorizedOperationException();
        }

        User userToDelete = bd.getUserByUsername(getToDeleteUsername());

        if (userToDelete == null)
            return;

        bd.removeUsers(userToDelete);
        userToDelete.delete();
    }

    public final String getUserToken() {
        return userToken;
    }

    public final String getToDeleteUsername() {
        return toDeleteUsername;
    }

}
