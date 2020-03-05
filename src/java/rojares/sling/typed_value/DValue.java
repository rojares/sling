package rojares.sling.typed_value;

import rojares.sling.SlingException;
import rojares.sling.typed_value.collection.DCollection;
import rojares.sling.typed_value.primitive.DPrimitive;

import java.util.regex.Pattern;

/**
 * DValue is the root interface for all values returned by David.
 */
public interface DValue {

    /**
     * Used internally to parse literals from the server.
     */
    public static DValue parse(String literal) {
        if (DType.primitivePattern.matcher(literal).lookingAt()) return DPrimitive.parse(literal);
        else if (DType.collectionPattern.matcher(literal).lookingAt()) return DCollection.parse(literal);
        else {
            throw new SlingException(
                "DValue did not start with a recognized type identifier. Response string was: " + literal
            );
        }
    }

    /**
     * Returns the type of the value.
     */
    public DType getType();
}
