package rojares.sling;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rojares.sling.typed_value.DType;
import rojares.sling.typed_value.DValue;
import rojares.sling.typed_value.collection.DTable;
import rojares.sling.typed_value.primitive.DBoolean;
import rojares.sling.typed_value.primitive.DInteger;
import rojares.sling.typed_value.primitive.DString;

import java.util.Map;
import java.util.regex.Matcher;

/**
 * DResult is a map that returns all those things that user himself asked the server to return using explicit return
 * operator in the deestarInput that he sent in the request.<br>
 * If the deestarInput was:
 * <pre>
 * return(x, /R1);
 * return(y, 23);
 * return(z, true);
 * </pre>
 * Then the DResult would contain 3 entries:
 * <pre>
 * x =&gt; DTable
 * y =&gt; DInteger
 * z =&gt; DBoolean
 * </pre>
 * Because the user himself defined what was included in the result he should know it's type but he can also ask the
 * of an entry with the getType method.
 */
public class DResult {

    Logger logger = LoggerFactory.getLogger(DResult.class);

    private Map<String, DValue> resultMap = new ListOrderedMap<String, DValue>();

    DResult(StringBuilder response) {
        if (response.length() == 0) return;
        logger.trace("DResult: {}", Sling.formatCtrlChars(response.toString()));
        // First we split the response by name_value pairs with FS (File Separator, 28)
        String[] nameValuePair = Sling.FS_Pattern.split(response);
        for (int i = 0; i<nameValuePair.length; i++) {
            logger.trace("DResult name-value pair {}: {}", i, Sling.formatCtrlChars(nameValuePair[i]));
            Matcher m = Sling.identifierPattern.matcher(nameValuePair[i]);
            if (m.lookingAt()) {
                String identifier = m.group().substring(0, m.group().length()-1);
                DValue value = DValue.parse(nameValuePair[i].substring(m.group().length()));
                resultMap.put(identifier, value);
            }
            else {
                throw new SlingException("Protocol error: Could not find identifier in string " + Sling.formatCtrlChars(nameValuePair[i]));
            }
        }
    }

    /**
     * Returns the type of the value associated with the name
     * @param name that was defined in the return statement that caused this value to be returned
     * @return DType is one of the supported types of David
     */
    public DType getType(String name) {
        return resultMap.get(name).getType();
    }

    /**
     * If the requested value is not DInteger then a ClassCastException is thrown which is a RuntimeException.
     * @return DInteger null means that the variable name did not exist in the result. If the associated value is null
     * it is stored as value of the returned DInteger.
     */
    public DInteger getInteger(String name) {
        return (DInteger) resultMap.get(name);
    }
    /**
     * If the requested value is not DBoolean then a ClassCastException is thrown which is a RuntimeException.
     * @return DBoolean null means that the variable name did not exist in the result. If the associated value is null
     * it is stored as value of the returned DBoolean.
     */
    public DBoolean getBoolean(String name) {
        return (DBoolean) resultMap.get(name);
    }
    /**
     * If the requested value is not DString then a ClassCastException is thrown which is a RuntimeException.
     * @return DString null means that the variable name did not exist in the result. If the associated value is null
     * it is stored as value of the returned DString.
     */
    public DString getString(String name) {
        return (DString) resultMap.get(name);
    }
    /**
     * If the requested value is not DTable then a ClassCastException is thrown which is a RuntimeException.
     * @return DTable null means that the variable name did not exist in the result.
     */
    public DTable getTable(String name) {
        return (DTable) resultMap.get(name);
    }

    /**
     * Outputs all returned variables, nicely formatted
     */
    public String toString() {
        StringBuilder sb = new StringBuilder("Result\n");
        for (Map.Entry<String, DValue> kv : resultMap.entrySet()) {
            sb.append(kv.getKey());
            sb.append(" = ");
            DValue v = kv.getValue();
            if (v.getType() == DType.TABLE) sb.append("\n");
            sb.append(v.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
