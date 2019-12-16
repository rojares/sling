package rojares.sling.type;

import rojares.sling.SlingException;
import rojares.sling.parser.ResponseParser;

import java.util.Iterator;

/**
 * HEADER
 *     primitive_type:identifier[US]primitive_type:identifier[US]...
 * [GS]
 * BODY
 *     primitive_literal[US]primitive_literal[US]...
 *     [RS]
 *     primitive_literal[US]primitive_literal[US]...
 *     ...
 * [GS]
 * TBD: DHeader, DBody, DRow
 */
public class DTable implements DValue, Iterable<DTuple> {

    final DHeader header;
    final DBody body;

    public DTable(String literal) {
        int start = indexAfter(literal, "HEADER\\s+", 0);
        int end = literal.indexOf(ResponseParser.GS, start);
        this.header = new DHeader(literal.substring(start, end));
        start = indexAfter(literal, "BODY\\s+", end+1);
        end = literal.indexOf(ResponseParser.GS, start);
        this.body = new DBody(literal.substring(start, end));
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
        }
    }



}
