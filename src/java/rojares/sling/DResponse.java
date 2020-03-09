package rojares.sling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;

/**
 * DResponse keeps reading from a socket until it receives EOT, a timeout occurs or the maximum response length is reached.
 * This method throws SlingException or it's subclass DavidException which are both runtime exceptions.
 */
public class DResponse {

    Logger logger = LoggerFactory.getLogger(DResponse.class);

    DResult result = null;

    DResponse(BufferedReader reader, DSessionParams params) {

        // First thing we do is read the whole response (everything until EOT) in String and based on the first
        // character we create either DResult or DError
        StringBuilder response = Sling.readUntil(reader, params, false);

        // First character of the input should always be ACK or NAK
        char chr = response.charAt(0);
        response.deleteCharAt(0); // delete the first character from the response after reading

        switch (chr) {
            case Sling.C_ACK:
                // this will throw SlingException if there is any issue with the response
                logger.trace("ACK response received is: {}", Sling.formatCtrlChars(response.toString()));
                this.result = new DResult(response);
                break;
            case Sling.C_NAK:
                /* If server returns NAK it means there was an error on the serverside and thus we construct a
                DavidException
                 */
                logger.trace("NAK response received is: {}", Sling.formatCtrlChars(response.toString()));
                throw DavidException.parse(response);
            default:
                // The first character must always be ACK or NAK but we have to prepare the client for any kind of input.
                throw new SlingException(
                    "Protocol error: Server response started with character (in decimal): " + chr +
                    "\nResponse must always start with ACK(6) or NAK(21)." +
                    Sling.formatCtrlChars(Sling.printErroneousResponse(response))
                );
        }
    }

    DResult getDResult() {
        return this.result;
    }
}
