package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;

public class SetPasswordWithUsernameService extends BubbleDocsService {

    private String username;
    private String password;

    public SetPasswordWithUsernameService(String token, String password) {
        this.username = token;
        this.password = password;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bubbleDocs = BubbleDocs.getInstance();
        User user = bubbleDocs.getUserByUsername(username);

        if (user == null)
            throw new InvalidUsernameException();

        user.setPassword(password);

    }

}
