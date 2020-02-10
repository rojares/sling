package rojares.sling;

import java.util.regex.Pattern;

/**
 * In David and thus Sling String type supports only Unicode Basic Multilingual Plane (BMP). The reason for this is
 * that most programming languages store the characters internally in UTF-16 and their string type does not work well
 * if you use characters outside of the BMP. Methods like length, reverse and split do not work correctly and great
 * care needs to be taken by the developer so as not to corrupt the string.
 * Perl 6 seems to give some support of dealing with characters outside of BMP defining methods like bytes, codes and
 * graphs instead of a length method. But it is not clear whether for example reverse works.
 * The most robust solution would be to store characters in UTF-32 in which case all of these issues could be solved.
 * And Python from version 3.3 onwards does excatly that.
 *
 * So for now David supports only BMP characters and if a String contains surrogate pairs An exception is raised.
 * Correctness above compatibility!
 * From business perspective this will cause problems only when exporting software to China.
 *
 * What this means is that surrogate pairs need to be removed from java's UTF-16 strings. However when strings are
 * encoded to network we use UTF-8 and the surrogate pair concept disappears. So in case the string that user sends
 * to server contains surrogate pairs which is the same as unicode characters (code points) outside of BMP then the
 * server throws the appropriate exception. The client does not check for those.
 *
 * The client
 *
 * This class contains all static variables and methods used in the library.
 */
public class Sling {

    /*
    Timeout for reading response
    If we have to wait idle 5 seconds and the EOT does not come we throw a timeout exception
     */
    public static int RESPONSE_TIMEOUT = 5000;

    // EOT = END OF TRANSMISSION, this is used to signal to server that the request is over and the client is waiting
    // for response
    public static String C_EOT = "\u0004";
    // When Sling inserts a newline it uses the unix convention
    public static String C_NEWLINE = "\n";
    static String C_ACK = "\u0006";
    static String C_NAK = "\u000F";

    // unicode defines 65 control characters, however David excludes from those control characters only first 32
    // control characters (the classic 0-31) and allows control characters between 128-159 that had printable
    // characters in CP-1252
    public static Pattern PTR_CC = Pattern.compile("[\\x00-\\x1F]+");
    // From these 32 control characters 3 are allowed as part of the string: TAB, CR and LF
    public static Pattern PTR_CC_EXCEPT_3 = Pattern.compile("[\\x00-\\x1F&&[^\\t\\r\\n]]+");
    // Newline is either LF (unix) alone or CRLF (windows) combination. Lonely CR is not considered newline.
    public static Pattern PTR_NEWLINE = Pattern.compile("\\r?\\n");

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
