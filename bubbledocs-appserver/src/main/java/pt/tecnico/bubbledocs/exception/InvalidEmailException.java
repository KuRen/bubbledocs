package pt.tecnico.bubbledocs.exception;

public class InvalidEmailException extends BubbleDocsException {
    private static final long serialVersionUID = 1L;

    public InvalidEmailException() {
        super();
    }

    public InvalidEmailException(String msg) {
        super(msg);
    }
}
