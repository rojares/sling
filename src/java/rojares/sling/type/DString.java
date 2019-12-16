package rojares.sling.type;

import rojares.sling.SlingException;

/**
 * DString is the same as Java String but without control characters except tab and linefeed
 * Unfortunately Java's string is still a little constrained to first plane unicode with it's 16-bit but the issue of
 * whether the characters of David are 32-bit is still an open issue. For now they are 16-bit.
 */
public class DString implements DValue {

    private String value;

    /**
     * Used to construct the string from a string literal
     * >sequence_of_zero_or_more_unicode_characters|NULL (null is case-insensitive)
     * So the string must either start by character > or be the string NULL
     * I am not doi
     */
    public DString(String literal) {
        if (literal.equalsIgnoreCase("NULL")) this.value = null;
        else if (literal.charAt('>') this.value = literal.substring(1); // everything after >, nothing needs escaping
        else throw new SlingException("String literal coming over network must start with > character.")
    }

    public String getString() {
        return this.value;
    }
}
