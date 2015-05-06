package pt.ulisboa.tecnico.sdis.store.client;

public class StoreClientException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public StoreClientException() {
    }

    public StoreClientException(String message) {
        super(message);
    }

    public StoreClientException(Throwable cause) {
        super(cause);
    }

    public StoreClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
