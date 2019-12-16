package rojares.sling.parser;

import rojares.sling.type.DValue;

import java.util.HashMap;
import java.util.Map;

public class ParsingContext {

    String identifier = null;
    Map<String, DValue> result = new HashMap<String, DValue>();

    void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    void flush(DValue value) {
        this.result.put(this.identifier, value);
        this.identifier = null;
    }
    Map<String, DValue> getResult() {
        return this.result;
    }
}
