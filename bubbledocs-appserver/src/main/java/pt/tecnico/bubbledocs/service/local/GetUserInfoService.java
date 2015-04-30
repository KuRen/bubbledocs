package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class GetUserInfoService extends BubbleDocsService {

    private String username;
    private User user;

    public GetUserInfoService(String username) {
        this.username = username;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bubbleDocs = getBubbleDocs();

        this.user = bubbleDocs.getUserByUsername(username);
    }

    public User getUser() {
        return user;
    }
}
