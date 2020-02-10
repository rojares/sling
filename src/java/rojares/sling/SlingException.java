package rojares.sling;

/**
 * Sling throws SlingExceptions when there is problem related to clientside.
 * DavidException extends this class and means that exception happened on the server side. It has more structure.
 */
public class SlingException extends RuntimeException {
    public SlingException(String message) {
        super(message);
    }
    public SlingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
