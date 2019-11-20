package rojares.sling;

/**
 * DResult has format: name=value[US]name=value[US]...[US]name=value
 * where name is an identifier (= ascii string) and value is a literal of a value conforming to one of
 * the types supported by David.
 * The currently supported literals are:
 * primitive literals:
 * INTEGER:sequence_of_one_or_more_digits_and_optional_minus_sign) | INTEGER:NULL
 * BOOLEAN:TRUE|FALSE | BOOLEAN:NULL
 * STRING:sequence_of_zero_or_more_unicode_characters | STRING:NULL
 * collection literals:
 * TABLE {
 *      HEADER [attribute_definition_list]
 *      BODY {
 *          [ primitive_literal, ...]*
 *      }
 *  }
 */
public class DResult {

    // US = Unit Separator, 1F
    static String US = "\u001F";

    Map<String, Object> resultMap = new HashMap<String, Object>();

    public DResult(BufferedReader in) throws SlingException {
        String[] values = SlingUtils.readUntilEOT(in).split(US);
        for(String value : values) {
            KeyValue kv = parse(value);
            resultMap.put(kv.getKey(), kv.getValue());
        }
    }

    private KeyValue parse(String value) {

    }

    public Long getInteger(String name) {

    }

    public Boolean getBoolean(String name) {

    }

    public String getString(String name) {

    }

    public DTable getTable(String name) {

    }

}
