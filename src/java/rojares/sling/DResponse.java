package rojares.sling;

import java.io.BufferedReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 */
public class DResponse {



    boolean isError = false;
    DResult result = null;
    DError error = null;

    /**
     * Resets all fields at the beginning so that the same DResponse object can be used multiple times
     * This method will read
     */
    public DResponse readResponse(BufferedReader in) throws SlingException {

        this.isError = false;
        this.result.reset();
        this.error = null;

        try {


            // First character of the input should always be ACK or NAK
            char ci = (char) in.read();
            switch (ci) {
                case Sling.C_ACK:
                    // read the result
                    this.result = this.result.readResult(in);
                    break;
                case Sling.C_NAK:
                    this.isError = true;
                    this.error = this.error.readError(in);
                    break;
                default:
                    // should never happen but maybe during development or deliberate attack
                    // let's try to read until EOT to clear out the rest of the response
                    ;
                    throw new SlingException("Server response could not be recognized. Unexpected response started with " +
                            "character: " + ci + " and continued like this: " + Sling.readUntilEOT(in));
            }
            return this;
        }
        catch (SocketTimeoutException ste) {
            throw new SlingException("Server's response stayed idle " + Sling.RESPONSE_TIMEOUT + "ms before EOT " +
                    "was encountered. Response received so far was: " + ci +
                    "recognized. Unexpected response " +
                    "started with " +
                    "character: " + ci + " and continued like this: " + Sling.readUntilEOT(in));
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
