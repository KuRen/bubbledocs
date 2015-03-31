package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class CreateUser extends BubbleDocsService {

    private String userToken;
    private String newUsername;
    private String password;
    private String name;

    public CreateUser(String userToken, String newUsername, String password, String name) {
        this.userToken = userToken;
        this.newUsername = newUsername;
        this.password = password;
        this.name = name;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        if (newUsername.isEmpty())
            throw new EmptyUsernameException();

        BubbleDocs bubbleDocs = getBubbleDocs();
        SessionManager sessionManager = bubbleDocs.getManager();

        sessionManager.cleanOldSessions();

        User user = sessionManager.findUserByToken(userToken);

        if (user == null)
            throw new UserNotInSessionException();

        if (!user.isRoot())
            throw new UnauthorizedOperationException();

        if (bubbleDocs.getUserByUsername(newUsername) != null)
            throw new DuplicateUsernameException();

        User newUser = new User(newUsername, password, name);
        bubbleDocs.addUsers(newUser);

    }

    public final String getUserToken() {
        return userToken;
    }

    public final String getNewUsername() {
        return newUsername;
    }

    public final String getPassword() {
        return password;
    }

    public final String getName() {
        return name;
    }
}
