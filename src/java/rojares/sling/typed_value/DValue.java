package rojares.sling.typed_value;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rojares.sling.Sling;
import rojares.sling.SlingException;
import rojares.sling.typed_value.collection.DCollection;
import rojares.sling.typed_value.primitive.DPrimitive;

/**
 * DValue is the root interface for all values returned by David.
 */
public interface DValue {

    Logger logger = LoggerFactory.getLogger(DValue.class);

    /**
     * Used internally to parse literals from the server.
     */
    public static DValue parse(String literal) {
        if (DType.primitivePattern.matcher(literal).lookingAt()) return DPrimitive.parse(literal);
        else if (DType.collectionPattern.matcher(literal).lookingAt()) return DCollection.parse(literal);
        else {
            throw new SlingException(
                "DValue did not start with a recognized type identifier (B:, I:, S:, T:). The value literal was: " + Sling.formatCtrlChars(literal)
            );
        }
    }

    /**
     * Returns the type of the value.
     */
    public DType getType();

    /**
     * @return the string literal of the value or java null if it was a null value.
     */
    public String toString();

}
