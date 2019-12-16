package rojares.sling;

import rojares.sling.parser.ResponseParser;
import rojares.sling.type.*;

import java.util.*;
import java.io.*;


public class DResult {

    Map<String, DValue> resultMap = new HashMap<String, DValue>();

    public DResult(BufferedReader in) {
        this.resultMap = ResponseParser.parse(in);
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
