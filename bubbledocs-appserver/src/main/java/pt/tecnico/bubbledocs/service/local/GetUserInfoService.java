package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.service.dto.UserInfo;

public class GetUserInfoService extends BubbleDocsService {

    private String username;
    private UserInfo userInfo;

    public GetUserInfoService(String username) {
        this.username = username;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bubbleDocs = BubbleDocs.getInstance();

        User user = bubbleDocs.getUserByUsername(username);

        if (user == null)
            throw new InvalidUsernameException();

        this.userInfo = new UserInfo(username, user.getName(), user.getEmail(), user.getPassword());
    }

    public UserInfo getUser() {
        return userInfo;
    }
}
