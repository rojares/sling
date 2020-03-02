package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.primitive.DPrimitive;
import rojares.sling.typed_value.DValue;

import java.util.HashMap;
import java.util.Map;

/**
 * primitive_literal[US]primitive_literal[US]...
 */
public class DRow {

    Map<String, DValue> datums = new HashMap<String, DValue>();

    public DRow(DHeader header, String literal) {
        String[] primitiveLiterals = DHeader.US_Pattern.split(literal);
        for(int i=0; i<primitiveLiterals.length;i++) {
            DPrimitive dp = DPrimitive.parse(primitiveLiterals[i]);
            DAttribute attr = header.getAttribute(i);
            if (attr.getType().equals(dp.getType())) {
                datums.put(attr.getName(), dp);
            }
            else {
                throw new SlingException(
                    "Type mismatch: Datum for attribute " + attr.getName() + "was of type " + dp.getType().name() +
                    "while the attribute was of type " + attr.getType().name()
                );
            }
        }
    }

}