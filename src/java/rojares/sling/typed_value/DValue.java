package rojares.sling.typed_value;

import rojares.sling.SlingException;
import rojares.sling.typed_value.collection.DCollection;
import rojares.sling.typed_value.primitive.DPrimitive;

import java.util.regex.Pattern;

/**
 * Parses all values that David supports
 * dvalue_literal := primitive_literal | collection_literal
 *
 * primitive_literal := boolean_literal | integer_literal | string_literal
 * boolean_literal := B:(TRUE|FALSE|NULL) (case-insensitive)
 * integer_literal := I:(-?[0-9]+|NULL) (null is case-insensitive)
 * string_literal := S:(>sequence_of_zero_or_more_unicode_characters|NULL) (null is case-insensitive)
 *
 * collection_literal := table_literal
 * table_literal :=
 * T:HEADER
 *     primitive_type:identifier[US]primitive_type:identifier[US]...
 * [GS]
 * BODY
 *     primitive_literal[US]primitive_literal[US]...
 *     [RS]
 *     primitive_literal[US]primitive_literal[US]...
 *     ...
 */
public interface DValue {

    public static Pattern primitivePattern = Pattern.compile("[BIS]:");
    public static Pattern collectionPattern = Pattern.compile("[T]:");

    public static DValue parse(String literal) {
        if (primitivePattern.matcher(literal).lookingAt()) return DPrimitive.parse(literal);
        else if (collectionPattern.matcher(literal).lookingAt()) return DCollection.parse(literal);
        else {
            throw new SlingException(
                "DValue did not start with a recognized type identifier. Response string was: " + literal
            );
        }
    }

    public DType getType();
}
