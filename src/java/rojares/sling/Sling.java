package rojares.sling;

import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.util.regex.Pattern;

/**
 * In David and thus Sling String type supports only Unicode Basic Multilingual Plane (BMP). The reason for this is
 * that most programming languages store the characters internally in UTF-16 and their string type does not work well
 * if you use characters outside of the BMP. Methods like length, reverse and split do not work correctly and great
 * care needs to be taken by the developer so as not to corrupt the string.
 * Perl 6 seems to give some support of dealing with characters outside of BMP defining methods like bytes, codes and
 * graphs instead of a length method. But it is not clear whether for example reverse works.
 * The most robust solution would be to store characters in UTF-32 in which case all of these issues could be solved.
 * And Python from version 3.3 onwards does exactly that.
 *
 * For now David supports only BMP characters and if a String contains surrogate pairs An exception is raised.
 * Correctness above compatibility!
 * From business perspective this will cause problems only when exporting software to China.
 *
 * If the user sends a string to server with surrogate pairs, they disappear when the string is encoded with utf-8 to
 * network. However when the server receives that string it realizes that it contains code points outside of BMP and it
 * will throw and exception. Therefore we don't bother checking the strings for surrogate pairs on the clientside.
 *
 * We could allow codepoints outside of BMP on the serverside but because the server is implemented in java that has
 * the above mentioned issues with codepoints outside of BMP we have decided to exclude them for now.
 *
 * This class contains all static variables and methods used in the library.
 */
public class Sling {

    /*
    Timeout for reading response
    If we have to wait idle 5 seconds and the EOT does not come we throw a timeout exception
     */
    public static int RESPONSE_TIMEOUT = 5_000;

    /*
    We set a limit to how large a response can be. This limit is expressed in characters (codepoints) in BMP.
    When encoded in utf-8 they take 1-3 bytes but when stored into memory they take 2 bytes.
    So the result will consume approximately 2 * limit of bytes in memory
    Now I have set the limit to 20 million characters which will consume around 40MB of memory.
     */
    public static int RESPONSE_MAX_SIZE_IN_CHARS = 20_000_000;

    // EOT = END OF TRANSMISSION, this is used to signal to server that the request is over and the client is waiting
    // for response
    public static char C_EOT = 4;
    // When Sling inserts a newline it uses only LF character
    public static char C_NEWLINE = 10;
    static char C_ACK = 6;
    static char C_NAK = 21;

    // unicode defines 65 control characters, however David excludes from those control characters only first 32
    // control characters (the classic 0-31) and allows control characters between 128-159 that had printable
    // characters in CP-1252
    public static Pattern PTR_CC = Pattern.compile("[\\x00-\\x1F]+");
    // From these 32 control characters 3 are allowed as part of the string: TAB, CR and LF
    public static Pattern PTR_CC_EXCEPT_3 = Pattern.compile("[\\x00-\\x1F&&[^\\t\\r\\n]]+");
    // Newline is either LF (unix) alone or CRLF (windows) combination. Lonely CR is not considered newline.
    public static Pattern PTR_NEWLINE = Pattern.compile("\\r?\\n");

    /**
     * Reads from a reader characters until it matches a certain character or timeout occurs or the char limit is exceeded.
     * Returns all read characters in StringBuilder unless discard was true in which case the StringBuilder is empty.
     */
    public static StringBuilder readUntil(Reader reader, char endChar, int maxlen, boolean discard) {

        // declare local variables
        StringBuilder response = new StringBuilder();
        int charCount = 0;
        char chr;

        try {
            while(true) {
                chr = readChar(reader, response);
                // response is fully received
                if (chr == endChar) return response;
                // a response character was received so we add it to our buffer
                if (!discard) response.append(chr);
                // check if the response is longer than allowed
                charCount++;
                if (charCount > maxlen) {
                    throw new SlingException(
                        "Protocol error: The response from server was too long. The upper size limit for the " +
                        "response was " + maxlen + " characters and the server should know that. " +
                        "Please contact the developer of David! " + printPartialResponseError(response)
                    );
                }
            }
        }
        catch (SocketTimeoutException ste) {
            throw new SlingException(
                "Input stream timed out after " + RESPONSE_TIMEOUT + "ms before the response was fully " +
                "received from server. " + printPartialResponseError(response)
            );
        }
        catch (IOException ioe) {
            throw new SlingException(
                "Input stream encountered IO error with message: " + ioe.getMessage() + "\n" +
                printPartialResponseError(response)
            );
        }
    }

    /*
    Reader's read method says that -1 is a possible return value and it signifies end-of-stream.
    Frankly I don't know what end-of-stream means. I suppose that if inputstream or socket is closed
    then it will throw IOException. This check is not good for performance so maybe it could be removed?
     */
    static char readChar(Reader reader, StringBuilder response) throws IOException {
        int ci = reader.read();
        if (ci == -1) {
            throw new SlingException(
                "Socket input stream was closed while trying to read response from server. " +
                printPartialResponseError(response)
            );
        }
        return (char) ci;
    }

    static String printPartialResponseError(StringBuilder resp) {
        int len = resp.length();
        int end = Math.min(len, 100);
        return
            "Partial response was " + len + " characters. " +
            "The start of the response received was:\n" + resp.substring(0, end)
        ;
    }

    /**
     * Checks for control characters
     */
    public static void checkForControlCharacters(String input) {
        if (PTR_CC.matcher(input).find())
            throw new SlingException("Input string in D* must not contain control characters in the range 0-31.");
    }

    /**
     * Checks for control characters except TAB, CR and LF
     */
    public static void checkForControlCharactersExcept3(String input) {
        if (PTR_CC_EXCEPT_3.matcher(input).find())
            throw new SlingException(
                "Input string in D* must not contain control characters in the range 0-31 except TAB, CR and LF."
            );
    }

}
