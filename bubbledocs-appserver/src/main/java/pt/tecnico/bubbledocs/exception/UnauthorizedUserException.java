package pt.tecnico.bubbledocs.exception;

public class UnauthorizedUserException extends BubbleDocsException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedUserException() {
        super("User is not authorized to perform this action.");
    }
    
    public UnauthorizedUserException(String message) {
    	super(message);
    }

}
