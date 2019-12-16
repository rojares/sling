package rojares.sling.type;

import rojares.sling.SlingException;

/**
 * DInteger is the same as Long in java which is a signed 64bit integer.
 */
public class DInteger implements DValue {

    private Long value;

    /**
     * Used to construct the integer from a string literal
     * -?[0-9]+|NULL (null is case-insensitive)
     * Copied from java.lang.Long documentation:
     * Parses the string argument as a signed decimal long. The characters in the string must all be decimal digits,
     * except that the first character may be an ASCII minus sign '-' (\u002D') to indicate a negative value
     * or an ASCII plus sign '+' ('\u002B') to indicate a positive value.
     * The resulting long value is returned, exactly as if the argument and the radix 10 were given as arguments
     * to the parseLong(java.lang.String, int) method.
     * NB. Long allows + sign in the beginnning but this is not accepted by David
     */
    public DInteger(String literal) {
        if (literal.equalsIgnoreCase("NULL")) this.value = null;
        else this.value = Long.valueOf(literal);
    }

    public Long getLong() {
        return this.value;
    }
    public long longValue() {
        if (this.value == null) throw new SlingException("Can not convert null value to primitive long.");
        else return this.value.longValue();
    }
    public String toString() {
        if (this.value == null) return null;
        else return this.value.toString();
    }
}
