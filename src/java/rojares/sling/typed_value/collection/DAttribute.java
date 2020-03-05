package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;

/**
 * DAttribute contains the type and name of an attribute
 */
public class DAttribute {

    private DType type;
    private String name;

    /**
     * Used internally to parse literals from the server.
     */
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

    /**
     * @return DType is the type of this attribute
     */
    public DType getType() {
        return this.type;
    }
    /**
     * @return String is the name of this attribute
     */
    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.type.name() + ":" + this.name;
    }
}
