package rojares.sling;


public class DResponse {

    static char ACK = '\u0006';
    static char NAK = '\u0015';

    boolean isError;
    DResult result;
    DError error;

    public DResponse(InputStreamReader in) throws SlingException {
        // First character of the input should always be ACK or NAK
        int ci = in.read();
        switch (ci) {
            case 6:
                // ACK
                this.isError = false;
                this.result = new DResult
                break;
            default:
                throw new java.lang.IllegalStateException("Unexpected value: " + ci);
        }
        if (ci == )
        isError;
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
