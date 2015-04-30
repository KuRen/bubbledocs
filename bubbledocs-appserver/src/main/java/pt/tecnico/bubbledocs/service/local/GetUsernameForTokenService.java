package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class GetUsernameForTokenService extends BubbleDocsService {

    private String token;
    private String username;

    public GetUsernameForTokenService(String token) {
        this.token = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        this.username = getLoggedInUser(token).getUsername();

    }

    public String getUsername() {
        return username;
    }

}
