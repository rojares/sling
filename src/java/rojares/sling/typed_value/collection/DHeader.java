package rojares.sling.typed_value.collection;

import rojares.sling.Sling;
import rojares.sling.typed_value.DType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DHeader is an internal object of DTable containing a list of DAttributes.
 */
public class DHeader {

    List<DAttribute> attributeList = new ArrayList<DAttribute>();
    private Map<String, DType> attributeMap = new HashMap<String, DType>();

    DHeader(String literal) {
        String[] attributeLiterals = Sling.US_Pattern.split(literal);
        for(int i=0; i<attributeLiterals.length;i++) {
            DAttribute attr = new DAttribute(attributeLiterals[i]);
            attributeList.add(attr);
            attributeMap.put(attr.getName(), attr.getType());
        }
    }

    int degree() {
        return attributeList.size();
    }

    DAttribute getAttribute(int attrNum) {
        return this.attributeList.get(attrNum);
    }
    String getAttributeName(int attrNum) {
        return this.attributeList.get(attrNum).getName();
    }
    DType getAttributeType(String attrName) {
        return this.attributeMap.get(attrName);
    }

    String[] toStringArray() {
        String[] arr = new String[degree()];
        for(int i=0; i<degree(); i++) {
            arr[i] = getAttribute(i).toString();
        }
        return arr;
    }
}
