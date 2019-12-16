package rojares.sling;


public class DResponse {

    static int ACK = 6;
    static int NAK = 15;

    boolean isError;
    DResult result;
    DError error;

    public DResponse(BufferedReader in) throws SlingException {
        // First character of the input should always be ACK or NAK
        int ci = in.read();
        switch (ci) {
            case ACK:
                this.isError = false;
                this.result = new DResult(in);
                break;
            case NAK:
                this.isError = true;
                this.error = new DError(in);
                break;
            default:
                throw new java.lang.IllegalStateException("Unexpected value: " + ci);
        }
    }

    public boolean isError() {
        return isError;
    }

    public SlingSuccess getDResult() {
        return result;
    }

    public DError getDError() {
        return error;
    }
}
