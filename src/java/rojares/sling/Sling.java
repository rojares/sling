package rojares.sling;

import java.io.IOException;
import java.io.Reader;
import java.net.SocketTimeoutException;
import java.nio.CharBuffer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sling class contains static helper methods used to implement Sling.
 * <p>
 * The user does not need to use it but I have put here some documentation about Sling library.
 * <p>
 * In David and thus Sling String type supports only Unicode Basic Multilingual Plane (BMP). The reason for this is
 * that most programming languages store the characters internally in UTF-16 and their string type does not work well
 * if you use characters outside of the BMP. Methods like length, reverse and split do not work correctly and great
 * care needs to be taken by the developer so as not to corrupt the string.
 * Perl 6 seems to give some support of dealing with characters outside of BMP defining methods like bytes, codes and
 * graphs instead of a length method. But it is not clear whether for example reverse works.
 * The most robust solution would be to store characters in UTF-32 in which case all of these issues could be solved.
 * And Python from version 3.3 onwards does exactly that.
 * <p>
 * For now David supports only BMP characters and if a String contains surrogate pairs An exception is raised.
 * Correctness above compatibility!
 * From business perspective this will cause problems only when exporting software to China.
 * <p>
 * If the user sends a string to server with surrogate pairs, they disappear when the string is encoded with utf-8 to
 * network. However when the server receives that string it realizes that it contains code points outside of BMP and it
 * will throw and exception. Therefore we don't bother checking the strings for surrogate pairs on the clientside.
 * <p>
 * We could allow codepoints outside of BMP on the serverside but because the server is implemented in java that has
 * the above mentioned issues with codepoints outside of BMP we have decided to exclude them for now.
 */
public class Sling {

    static Logger logger = LoggerFactory.getLogger(Sling.class);

    /**
     * EOT = END OF TRANSMISSION, this is used to signal to server that the request is over and the client is waiting
     */
    public static char C_EOT = 4;
    /**
     * When Sling inserts a newline it uses only LF character
     */
    public static final char C_NEWLINE = 10;
    /**
     * Control character ACK hex:06
     */
    public static final char C_ACK = 6;
    /**
     * Control character NAK hex:15
     */
    public static final char C_NAK = 21;

    /**
     * unicode defines 65 control characters, however David excludes from those control characters only first 32
     * control characters (the classic 0-31) and allows control characters between 128-159 that had printable
     * characters in CP-1252
     */
    public static final Pattern PTR_CC = Pattern.compile("[\\x00-\\x1F]+");
    /**
     * From these 32 control characters 3 are allowed as part of the string: TAB, CR and LF
     */
    public static final Pattern PTR_CC_EXCEPT_3 = Pattern.compile("[\\x00-\\x1F&&[^\\t\\r\\n]]+");
    /**
     * Control character FS hex:1C
     */
    public static Pattern FS_Pattern = Pattern.compile("[\u001C]");
    /**
     * Identifier pattern is [a-zA-Z_][a-zA-Z_0-9]*= (includes the equal sign)
     */
    public static Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*=");

    /**
     * Control character FS hex:1F
     */
    public static Pattern US_Pattern = Pattern.compile("[\u001F]");

    /**
     * Reads from a reader characters until it matches a certain character or timeout occurs or the char limit is exceeded.
     * Returns all read characters in StringBuilder unless discard was true in which case the StringBuilder is empty.
     */
    public static StringBuilder readUntil(Reader reader, DSessionParams params, boolean discard) {

        // declare local variables
        StringBuilder response = new StringBuilder();
        int charCount = 0;
        char chr;

        try {
            while(true) {
                chr = readChar(reader, response);
                // response is fully received
                if (chr == C_EOT) {
                    return response;
                }
                // a response character was received so we add it to our buffer
                if (!discard) {
                    //logger.trace("Char {} added to the response.", Sling.formatCtrlChars(chr));
                    response.append(chr);
                }
                // check if the response is longer than allowed
                charCount++;
                if (charCount > params.getMaxResponseSize()) {
                    throw new SlingException(
                        "Protocol error: The response from server was too long. The upper size limit for the " +
                        "response was " + params.getMaxResponseSize() + " characters and the server should know that. " +
                        "Please contact the developer of David! " + printErroneousResponse(response)
                    );
                }
            }
        }
        catch (SocketTimeoutException ste) {
            throw new SlingException(
                "Input stream timed out after " + params.getTimeout() + "ms before the response was fully " +
                "received from server. " + printErroneousResponse(response)
            );
        }
        catch (IOException ioe) {
            throw new SlingException(
                "Input stream encountered IO error with message: " + ioe.getMessage() + "\n" +
                printErroneousResponse(response)
            );
        }
    }

    private static String formatCtrlChars(StringBuilder sb) {
        if (sb.length() == 0) return "[EMPTY]";
        for(int i=0; i<sb.length(); i++) {
            int cp = sb.codePointAt(i);
            if (cp < 32) {
                String s;
                int incr;
                if (cp == 4) s = "[EOT]";
                else if (cp == 6) s = "[ACK]";
                else if (cp == 9) s = "[TAB]";
                else if (cp == 10) s = "[LF]";
                else if (cp == 13) s = "[CR]";
                else if (cp == 21) s = "[NAK]";
                else if (cp == 28) s = "[FS]";
                else if (cp == 29) s = "[GS]";
                else if (cp == 30) s = "[RS]";
                else if (cp == 31) s = "[US]";
                else s = "[" + Integer.toHexString(cp).toUpperCase() + "]";
                sb.replace(i, i + 1, s);
                i += s.length() - 1;
            }
        }
        return sb.toString();
    }
    public static String formatCtrlChars(String s) {
        return formatCtrlChars(new StringBuilder(s));
    }
    public static String formatCtrlChars(char chr) {
        return formatCtrlChars(new StringBuilder().append(chr));
    }

    /**
     * Reader's read method says that -1 is a possible return value and it signifies end-of-stream.
     * Frankly I don't know what end-of-stream means. I suppose that if inputstream or socket is closed
     * then it will throw IOException. This check is not good for performance so maybe it could be removed?
     */
    static char readChar(Reader reader, StringBuilder response) throws IOException {
        int ci = reader.read();
        if (ci == -1) {
            throw new SlingException(
                "Socket input stream was closed while trying to read response from server. " +
                printErroneousResponse(response)
            );
        }
        return (char) ci;
    }

    /**
     * Prints the first 100 characters of the erroneous response (or less if the response is shorter).
     */
    public static String printErroneousResponse(StringBuilder resp) {
        int len = resp.length();
        int end = Math.min(len, 100);
        return
            "Erroneous response was " + len + " characters. " +
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
