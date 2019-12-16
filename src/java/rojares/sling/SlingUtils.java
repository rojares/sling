package rojares.sling;

import java.util.regex.Pattern;

public class SlingUtils {
    // unicode defines 65 control characters
    private static Pattern MATCH_CONTROL_CHARACTERS = Pattern.compile("[\\x00-\\x1F\\x7F-\\x9F]+");
    // From these 65 control characters 3 are needed TAB and LF
    private static Pattern MATCH_CONTROL_CHARACTERS_EXCEPT_TAB_LF = Pattern.compile("[\\x00-\\x1F\\x7F-\\x9F" +
            "&&[^\\t\\n]]+");

    public static String removeControlCharacters(String input) {
        return input.replaceAll(MATCH_ESCAPES, "");
    }

    /**
     * Removes all control characters except TAB and LF
     * Combinations CRLF become LF and this is correct because Sling network protocol considers only LF as a newline
     * character.
     * @param input any string
     * @return normalized string
     */
    public static String normalizeInput(String input) {
        return input.replaceAll(MATCH_CONTROL_CHARACTERS_EXCEPT_TAB_LF, "");
    }

}
