package rojares.sling.typed_value.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * DBody is an internal object of DTable containing DRows.
 */
public class DBody {

    DHeader header;
    List<DRow> rows = new ArrayList<DRow>();
    private static Pattern RS_Pattern = Pattern.compile("[\u001E]");

    DBody(DHeader header, String literal) {
        this.header = header;
        String[] rows = RS_Pattern.split(literal);
        for(int i=0; i<rows.length;i++) {
            this.rows.add(new DRow(header, rows[i]));
        }
    }

    int cardinality() {
        return rows.size();
    }

    DRow getRow(int rowNum) {
        return this.rows.get(rowNum);
    }

    String[][] to2DStringArray() {
        String[][] arr = new String[cardinality()][this.header.degree()];
        for(int i=0; i<cardinality(); i++) {
            DRow row = rows.get(i);
            for (int j = 0; j < this.header.degree(); j++) {
                arr[i][j] = row.getPrimitive(i).toString();
            }
        }
        return arr;
    }
}
