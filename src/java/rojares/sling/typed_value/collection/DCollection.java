package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DValue;

/**
 * collection_literal := T:table_literal
 */
public interface DCollection extends DValue {

    public static DCollection parse(String literal) {

        if (literal.startsWith("T:")) {
            return new DTable(literal.substring(2));
        }
        else {
            throw new SlingException(
                "DValue did not start with a recognized collection type identifier. Response string was: " + literal
            );
        }
    }

}
