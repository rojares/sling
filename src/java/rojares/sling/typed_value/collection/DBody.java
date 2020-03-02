package rojares.sling.typed_value.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * primitive_literal[US]primitive_literal[US]...
 * [RS]
 * primitive_literal[US]primitive_literal[US]...
 * ...
 */
public class DBody {

    List<DRow> rows = new ArrayList<DRow>();
    private static Pattern RS_Pattern = Pattern.compile("[\u001E]");

    public DBody(DHeader header, String literal) {
        String[] rows = RS_Pattern.split(literal);
        for(int i=0; i<rows.length;i++) {
            this.rows.add(new DRow(header, rows[i]));
        }
    }

    public int size() {
        return rows.size();
    }

    public DRow getRow(int rowNum) {
        return this.rows.get(rowNum);
    }
}
