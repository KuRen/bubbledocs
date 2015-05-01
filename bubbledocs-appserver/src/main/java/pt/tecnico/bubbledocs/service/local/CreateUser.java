package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyValueException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class CreateUser extends BubbleDocsService {
    private String userToken;
    private String newUsername;
    private String email;
    private String name;

    public CreateUser(String userToken, String newUsername, String email, String name) {
        this.userToken = userToken;
        this.newUsername = newUsername;
        this.email = email;
        this.name = name;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        if (newUsername == null || newUsername.isEmpty())
            throw new EmptyUsernameException();

        if (email == null || email.isEmpty())
            throw new EmptyValueException();

        BubbleDocs bubbleDocs = getBubbleDocs();
        SessionManager sessionManager = bubbleDocs.getSessionManager();

        sessionManager.cleanOldSessions();

        User user = sessionManager.findUserByToken(userToken);

        if (user == null)
            throw new UserNotInSessionException();

        if (!user.isRoot())
            throw new UnauthorizedOperationException();
        
        if(bubbleDocs.getUserByUsername(newUsername) != null)
            throw new DuplicateUsernameException();
        
        if(bubbleDocs.getUserByEmail(email) != null)
            throw new DuplicateEmailException();

        User newUser = new User(newUsername, null, email, name);
        bubbleDocs.addUsers(newUser);

    }

    public final String getUserToken() {
        return userToken;
    }

    public final String getNewUsername() {
        return newUsername;
    }

    public final String getEmail() {
        return email;
    }

    public final String getName() {
        return name;
    }
}
