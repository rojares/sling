package rojares.sling;


public class DResponse {

    boolean isError;
    SlingSuccess slingSuccess;
    SlingException slingException;

    public DResponse(boolean isError, SlingSuccess slingSuccess, SlingException slingException) {
        this.isError = isError;
        this.slingSuccess = slingSuccess;
        this.slingException = slingException;
    }

    public boolean isError() {
        return isError;
    }

    public SlingSuccess getSlingSuccess() {
        return slingSuccess;
    }

    public SlingException getSlingException() {
        return slingException;
    }
}
