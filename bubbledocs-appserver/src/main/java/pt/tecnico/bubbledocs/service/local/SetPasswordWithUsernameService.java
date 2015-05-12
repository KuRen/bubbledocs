package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class SetPasswordWithUsernameService extends BubbleDocsService {

    private String username;
    private String password;

    public SetPasswordWithUsernameService(String token, String password) {
        this.username = token;
        this.password = password;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        User user = getBubbleDocs().getUserByUsername(username);

        user.setPassword(password);

    }

}
