package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;

public class RenewPassword extends BubbleDocsService {

    private String token;
    private String password;

    public RenewPassword(String token) {
        this.token = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (token == null || token.isEmpty()) {
            throw new InvalidArgumentException();
        }

        User user = getLoggedInUser(token);

        password = user.getPassword();
        user.setPassword(null);
    }

    public String getRemovedPassword() {
        return password;
    }
}
