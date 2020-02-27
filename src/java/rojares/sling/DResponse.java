package rojares.sling;

import java.io.BufferedReader;
import java.net.SocketTimeoutException;

/**
 *
 */
public class DResponse {

    boolean isError = false;
    DResult result = null;
    DError error = null;

    /**
     * DResponse keeps reading from a socket until it receives EOT or a timeout occurs.
     * In order to avoid OOM exception we also set a limit to how many characters a response can contain.
     * Resets all fields at the beginning so that the same DResponse object can be used multiple times
     * This method will read
     * This method throws SlingException or it's subclass DavidException which are both runtime exceptions.
     */
    public DResponse(BufferedReader reader) {
        try {
            // First thing we do is read the whole response (everything until EOT) in String and based on the first
            // character we create either DResult or DError
            Sling.readUntil(reader, Sling.C_EOT, )
            // First character of the input should always be ACK or NAK
            int ci = Sling.readChar(reader, ;
            if (ci == -1) throw new SlingException("Socket input stream was closed while trying to read response from server.");
            char chr = (char) ci;
            switch (ci) {
                case Sling.C_ACK:
                    // this will throw SlingException if there is any issue with the response
                    this.result = this.result.readResult(reader);
                    break;
                case Sling.C_NAK:
                    this.isError = true;
                    // this will throw SlingException if there is any issue with the response
                    this.error = this.error.readError(reader);
                    break;
                default:
                    // The first character must always be ACK or NAK but we have to prepare the client for any kind of input.
                    // let's try to read until EOT to clear out the rest of the response
                    try {
                        String restOfTheResponse = Sling.readUntilEOT(reader);
                        throw new SlingException(
                            "Protocol error: Server response started with character (in decimal): " + chr + "\n" +
                            "Response must always start with ACK(6) or NAK(21)." +
                            "The rest of the response was:\n" + restOfTheResponse
                        );
                    } catch (SlingException se) {
                        throw new SlingException(
                            "Protocol error: Server response started with character (in decimal): " + chr +
                            "\nResponse must always start with ACK(6) or NAK(21)." +
                            "Unable to read the rest of the response because:\n" + se.getMessage()
                        );
                    }
                    break;
            }
            return this;
        }
        catch (SocketTimeoutException ste) {
            throw new SlingException(
                "Input stream timed out after " + Sling.RESPONSE_TIMEOUT + "ms before even the first character was " +
                "received from server."
            );
        }

    }

    public boolean isError() {
        return this.isError;
    }

    public DResult getDResult() {
        return this.result;
    }

    public DError getDError() {
        return this.error;
    }
}
