package rojares.sling.parser;

import rojares.sling.DSession;
import rojares.sling.SlingException;
import rojares.sling.type.*;

import java.io.Reader;

public class DValueParser {

    Reader reader;
    ParsingContext ctx;
    StringBuilder buffer = new StringBuilder();

    DValueParser(Reader reader, ParsingContext ctx) {
        this.reader = reader;
        this.ctx = ctx;
    }

    // let's read until FS (file sepatator) or EOT (end of transmission) and trim the result.
    // Then when we have the full dvalue we can try to recognize it.
    boolean parse() {
        int c = reader.read();
        while ( c!=-1 && c!= ResponseParser.FS) {
            buffer.append((char) c);
        }
        ctx.flush(parse(buffer.toString().trim()));
        if (c==-1) return true;
        else return false;
    }

    DValue parseDValue(String value) {
        if (value.startsWith("B:")) return new DBoolean(value.substring(2));
        else if (value.startsWith("I:")) return new DInteger(value.substring(2));
        else if (value.startsWith("S:")) return new DString(value.substring(2));
        else if (value.startsWith("T:")) return new DTable(value.substring(2));
        else throw new SlingException("...");
    }

}
