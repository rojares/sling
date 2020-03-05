package rojares.sling.typed_value;

import java.util.regex.Pattern;

/**
 * DType enumerates all currently supported types of David
 */
public enum DType {
    BOOLEAN, INTEGER, STRING, TABLE;
    static Pattern primitivePattern = Pattern.compile("[BIS]:");
    static Pattern collectionPattern = Pattern.compile("[T]:");
}
