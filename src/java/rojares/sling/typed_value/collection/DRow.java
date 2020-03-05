package rojares.sling.typed_value.collection;

import rojares.sling.Sling;
import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;
import rojares.sling.typed_value.primitive.DBoolean;
import rojares.sling.typed_value.primitive.DInteger;
import rojares.sling.typed_value.primitive.DPrimitive;
import rojares.sling.typed_value.primitive.DString;

import java.util.HashMap;
import java.util.Map;

/**
 * DRow is an ordered version of tuples in David.
 * attrPosition starts from 0.
 */
public class DRow {

    DHeader header;
    Map<String, DPrimitive> datums = new HashMap<String, DPrimitive>();

    public DRow(DHeader header, String literal) {
        this.header = header;
        String[] primitiveLiterals = Sling.US_Pattern.split(literal);
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

    public DType getType(int attrPosition) {
        return getType(this.header.getAttributeName(attrPosition));
    }
    public DType getType(String attrName) {
        return this.header.getAttributeType(attrName);
    }

    public DInteger getInteger(String attrName) {
        return (DInteger) datums.get(attrName);
    }
    public DInteger getInteger(int attrPosition) {
        return (DInteger) datums.get(this.header.getAttributeName(attrPosition));
    }

    public DBoolean getBoolean(String attrName) {
        return (DBoolean) datums.get(attrName);
    }
    public DBoolean getBoolean(int attrPosition) {
        return (DBoolean) datums.get(this.header.getAttributeName(attrPosition));
    }

    public DString getString(String attrName) {
        return (DString) datums.get(attrName);
    }
    public DString getString(int attrPosition) {
        return (DString) datums.get(this.header.getAttributeName(attrPosition));
    }

    public DPrimitive getPrimitive(String attrName) {
        return datums.get(attrName);
    }
    public DPrimitive getPrimitive(int attrPosition) {
        return datums.get(this.header.getAttributeName(attrPosition));
    }


}