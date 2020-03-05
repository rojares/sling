package rojares.sling.typed_value.primitive;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;
import rojares.sling.typed_value.DValue;

/**
 * DString is the same as Java String but without control characters except TAB, CR and LF
 * Because support for characters outside of BMP is limited they are not allowed
 * (meaning that David rejects surragate characters).
 */
public class DString implements DPrimitive {

    private String value;

    /**
     * Used to construct the string from a string literal
     * &gt;sequence_of_zero_or_more_unicode_characters|NULL (null is case-insensitive)
     * So the string must either start by character &gt; or be the string NULL
     * I am not doi
     */
    public DString(String literal) {
        if (literal.equalsIgnoreCase("NULL")) this.value = null;
        else if (literal.startsWith(">")) this.value = literal.substring(1); // everything after >, nothing needs escaping
        else throw new SlingException("String literal coming over network must start with > character.");
    }

    public DType getType() {
        return DType.STRING;
    }

    public String getString() {
        return this.value;
    }
}
