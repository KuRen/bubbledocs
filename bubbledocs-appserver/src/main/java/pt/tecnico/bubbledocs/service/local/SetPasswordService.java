package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class SetPasswordService extends BubbleDocsService {

    private String token;
    private String password;

    public SetPasswordService(String token, String password) {
        this.token = token;
        this.password = password;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        User user = getLoggedInUser(token);

        user.setPassword(password);

    }

}
