package rojares.sling.typed_value.primitive;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DValue;

/**
 * DPrimitive is the parent interface of all primitive values that David supports.
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
