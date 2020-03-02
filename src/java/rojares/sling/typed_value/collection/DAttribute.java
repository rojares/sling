package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;
import rojares.sling.typed_value.primitive.DBoolean;
import rojares.sling.typed_value.primitive.DInteger;
import rojares.sling.typed_value.primitive.DPrimitive;
import rojares.sling.typed_value.DValue;
import rojares.sling.typed_value.primitive.DString;

import java.util.HashMap;
import java.util.Map;

/**
 * primitive_type:identifier
 */
public class DAttribute {

    DType type;
    String name;

    public DAttribute(String literal) {
        if (literal.startsWith("B:")) {
            this.type = DType.BOOLEAN;
        }
        else if (literal.startsWith("I:")) {
            this.type = DType.INTEGER;
        }
        else if (literal.startsWith("S:")) {
            this.type = DType.STRING;
        }
        else {
            throw new SlingException(
                "DAttribute did not start with a recognized primitive type identifier. The literal was: " + literal
            );
        }
        this.name = literal.substring(2);
    }

    public DType getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
