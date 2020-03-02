package rojares.sling.typed_value.collection;

import rojares.sling.SlingException;
import rojares.sling.typed_value.DType;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HEADER
 *     primitive_type:identifier[US]primitive_type:identifier[US]...
 * [GS]
 * BODY
 *     primitive_literal[US]primitive_literal[US]...
 *     [RS]
 *     primitive_literal[US]primitive_literal[US]...
 *     ...
 */
public class DTable implements DCollection, Iterable<DRow> {

    final DHeader header;
    final DBody body;
    private static Pattern tableLiteralPattern = Pattern.compile(
        "\\s*HEADER\\s+([^\u001D]+)[\u001D]\\s*BODY\\s+(.*)"
    );

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

    public Iterator<DRow> iterator() {
        return new Iterator<DRow>() {
            int rowNum = 0;
            @Override
            public boolean hasNext() {
                return (rowNum < body.size());
            }

            @Override
            public DRow next() {
                rowNum++;
                return body.getRow(rowNum);
            }
        };
    }


    @Override
    public DType getType() {
        return DType.TABLE;
    }
}
