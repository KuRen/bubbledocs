package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UnknownBubbleDocsUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

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
    	SessionManager sm = bd.getManager();
    	sm.cleanOldSessions();
    	String username = sm.findUserByToken(getUserToken());
    	if(username == null) throw new UserNotInSessionException();
    	if(username.equals("root")) {
    		User user = bd.getUserByUsername(getToDeleteUsername());
    		if(user == null) throw new UnknownBubbleDocsUserException();
    		bd.removeUsers(user);
    		user.delete();
    	}
        else throw new UnauthorizedOperationException();
    }
    
    public final String getUserToken() {
        return userToken;
    }
    
    public final String getToDeleteUsername() {
        return toDeleteUsername;
    }
    
}
