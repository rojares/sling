package rojares.sling;

import rojares.sling.typed_value.*;
import rojares.sling.typed_value.collection.DTable;
import rojares.sling.typed_value.primitive.DBoolean;
import rojares.sling.typed_value.primitive.DInteger;
import rojares.sling.typed_value.primitive.DString;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This is very close to json but I like to do it myself to keep total control. Also I use now control characters so
 * that I can avoid more complex parsing, delimiters and escapes
 *
 * DResult := [empty] | name_value_list
 * name_value_list := name_value[FS]name_value[FS]...
 * name_value := identifier=dvalue_literal
 * identifier := [a-zA-Z_][a-zA-Z_0-9]*
 */
public class DResult {

    private Map<String, DValue> resultMap = new HashMap<String, DValue>();

    public static Pattern FS_Pattern = Pattern.compile("[\u001C]");
    public static Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*=");

    public DResult(StringBuilder response) {
        if (response.length() == 0) return;
        // First we split the response by name_value pairs with FS (File Separator, 28)
        String[] nameValuePair = FS_Pattern.split(response);
        for (int i = 0; i<nameValuePair.length; i++) {
            Matcher m = identifierPattern.matcher(nameValuePair[i]);
            if (m.lookingAt()) {
                String identifier = m.group().substring(0, m.group().length()-1);
                DValue value = DValue.parse(nameValuePair[i].substring(m.group().length()+1));
            }
            else {
                throw new SlingException("Protocol error: Could not find identifier in string " + nameValuePair[i]);
            }
        }
    }

    public DType getType(String name) {
        return resultMap.get(name).getType();
    }

    /**
     * If the requested value is not of the specified type then a ClassCastException is thrown which is a
     * RuntimeException.
     * @return null means that the variable name did not exist in the result. If the associated value was null it is
     * stored as value of the returned DInteger
     */
    public DInteger getInteger(String name) {
        return (DInteger) resultMap.get(name);
    }

    public DBoolean getBoolean(String name) {
        return (DBoolean) resultMap.get(name);
    }

    public DString getString(String name) {
        return (DString) resultMap.get(name);
    }

    public DTable getTable(String name) {
        return (DTable) resultMap.get(name);
    }

}
