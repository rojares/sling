package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DValue;

/**
 * DCollection is the parent interface of all collection values that David supports. Currently only DTable is supported.
 */
public interface DCollection extends DValue {

    /**
     * Used internally to parse literals from the server.
     */
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
