package pt.tecnico.bubbledocs.exception;

public class InvalidUsernameException extends BubbleDocsException {
    private static final long serialVersionUID = 1L;

    public InvalidUsernameException() {
        super();
    }

    public InvalidUsernameException(String msg) {
        super(msg);
    }
}
