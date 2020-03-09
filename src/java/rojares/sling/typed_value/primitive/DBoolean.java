package rojares.sling.typed_value.primitive;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;
import rojares.sling.typed_value.DValue;

/**
 * DBoolean is the same as Boolean in java which has 3 possible values: true, false and null
 */
public class DBoolean implements DPrimitive {

    private Boolean value;

    /**
     * Used to construct the integer from a string literal
     * TRUE|FALSE|NULL (case-insensitive)
     * Boolean allows in the place of FALSE any kind of string but David does not.
     */
    public DBoolean(String literal) {
        if (literal.equalsIgnoreCase("NULL")) this.value = null;
        else this.value = Boolean.valueOf(literal);
    }

    public Boolean getBoolean() {
        return this.value;
    }
    public boolean booleanValue() {
        if (this.value == null) throw new SlingException("Can not convert null value to primitive boolean.");
        else return this.value.booleanValue();
    }

    public DType getType() {
        return DType.BOOLEAN;
    }

    /**
     * @return true or false in lowercase because that's what java.lang.Boolean does, or java null
     */
    public String toString() {
        if (this.value == null) return null;
        else return this.value.toString();
    }
}
