package rojares.sling.typed_value.primitive;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DValue;

/**
 * primitive_literal := boolean_literal | integer_literal | string_literal
 * boolean_literal := B:(TRUE|FALSE|NULL) (case-insensitive)
 * integer_literal := I:(-?[0-9]+|NULL) (null is case-insensitive)
 * string_literal := S:(>sequence_of_zero_or_more_unicode_characters|NULL) (null is case-insensitive)
 */
public interface DPrimitive extends DValue {

    public static DPrimitive parse(String literal) {

        if (literal.startsWith("B:")) {
            return new DBoolean(literal.substring(2));
        }
        else if (literal.startsWith("I:")) {
            return new DInteger(literal.substring(2));
        }
        else if (literal.startsWith("S:")) {
            return new DString(literal.substring(2));
        }
        else {
            throw new SlingException(
                    "DValue did not start with a recognized primitive type identifier. Response string was: " + literal
            );
        }
    }

}
