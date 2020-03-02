package rojares.sling.typed_value.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * header_literal = attribute_literal[US]attribute_literal[US]...
 */
public class DHeader {

    List<DAttribute> attributes = new ArrayList<DAttribute>();
    static Pattern US_Pattern = Pattern.compile("[\u001F]");

    public DHeader(String literal) {
        String[] attributeLiterals = US_Pattern.split(literal);
        for(int i=0; i<attributeLiterals.length;i++) {
            attributes.add(new DAttribute(attributeLiterals[i]));
        }
    }

    public int size() {
        return attributes.size();
    }

    public DAttribute getAttribute(int attrNum) {
        return this.attributes.get(attrNum);
    }

}
