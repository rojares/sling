package rojares.sling;

import java.io.BufferedReader;

/**
 * DResponse keeps reading from a socket until it receives EOT, a timeout occurs or the maximum response length is reached.
 * This method throws SlingException or it's subclass DavidException which are both runtime exceptions.
 */
public class DResponse {

    DResult result = null;

    DResponse(BufferedReader reader, DSessionParams params) {

        // First thing we do is read the whole response (everything until EOT) in String and based on the first
        // character we create either DResult or DError
        StringBuilder response = Sling.readUntil(reader, params, false);

        // First character of the input should always be ACK or NAK
        char chr = response.charAt(0);
        response.deleteCharAt(0);
        switch (chr) {
            case Sling.C_ACK:
                // this will throw SlingException if there is any issue with the response
                this.result = new DResult(response);
                break;
            case Sling.C_NAK:
                /* If server returns NAK it means there was an error on the serverside and thus we construct a
                DavidException
                 */
                throw DavidException.parse(response);
            default:
                // The first character must always be ACK or NAK but we have to prepare the client for any kind of input.
                throw new SlingException(
                    "Protocol error: Server response started with character (in decimal): " + chr +
                    "\nResponse must always start with ACK(6) or NAK(21)." +
                    Sling.printErroneousResponse(response)
                );
        }
    }

    DResult getDResult() {
        return this.result;
    }
}
