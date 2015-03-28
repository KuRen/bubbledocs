package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
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
    	
    	if(getNewUsername().equals("")) throw new EmptyUsernameException();
    	
    	BubbleDocs bd = BubbleDocs.getInstance();
    	SessionManager sm = bd.getManager();
    	sm.cleanOldSessions();
    	String username = sm.findUserByToken(getUserToken());
    	if(username == null) throw new UserNotInSessionException();
        if(username.equals("root")) {
        	for(User u : bd.getUsersSet()) {
        		if(getNewUsername().equals(u.getUsername())) {
        			throw new DuplicateUsernameException();
        		}
        	}
        	User user = new User(getNewUsername(),getPassword(), getName());
        	bd.addUsers(user);
        }
        else throw new UnauthorizedUserException();
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
