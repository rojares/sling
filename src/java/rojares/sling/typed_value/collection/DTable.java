package rojares.sling.typed_value.collection;

import com.github.freva.asciitable.AsciiTable;
import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DTable is the only collection type returned by David.<br>
 * DTable has a header with list of attributes.<br>
 * It has also a body that contains a list of rows.<br>
 * And rows have datums that are in the same order as attributes are in the header.<br>
 * Example:
 * <pre>
 *     for(DRow row : dtable.iterator()) {
 *         row.getInteger("a");
 *         row.getBoolean("b");
 *         row.getString("c");
 *     }
 * </pre>
 */
public class DTable implements DCollection, Iterable<DRow> {

    private DHeader header;
    private DBody body;
    private static Pattern tableLiteralPattern = Pattern.compile(
        "\\s*HEADER\\s+([^\u001D]+)[\u001D]\\s*BODY\\s+(.*)"
    );

    /**
     * Only Sling uses the construct DTable from a literal received from the server
     */
    public DTable(String literal) {
        Matcher m = tableLiteralPattern.matcher(literal);
        if (m.matches()) {
            this.header = new DHeader(m.group(1));
            this.body = new DBody(this.header, m.group(1));
        }
        else {
            throw new SlingException("Unable to parse DTable literal. The literal was: " + literal);
        }
    }

    /**
     * Use the iterator to iterate over all rows in the table.
     */
    public Iterator<DRow> iterator() {
        return new Iterator<DRow>() {
            int rowNum = 0;
            @Override
            public boolean hasNext() {
                return (rowNum < body.cardinality());
            }

            @Override
            public DRow next() {
                rowNum++;
                return body.getRow(rowNum);
            }
        };
    }

    /**
     * The Type of DTable is DType.TABLE
     */
    @Override
    public DType getType() {
        return DType.TABLE;
    }

    /**
     * Retrieve from the header the attribute type of attribute that has name attrName
     */
    public DType getAttributeType(String attrName) {
        return this.header.getAttributeType(attrName);
    }

    /**
     * Get all attributes contained in the header
     */
    public List<DAttribute> getAttributes() {
        return this.header.attributeList;
    }

    /**
     * Prints the DTable in ascii table
     */
    public String toString() {
        return AsciiTable.getTable(this.header.toStringArray(), this.body.to2DStringArray());
    }
}
