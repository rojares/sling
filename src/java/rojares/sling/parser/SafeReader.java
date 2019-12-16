package rojares.sling.parser;

import rojares.sling.DSession;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Removes illegal control characters and turns EOT into -1 for easier stream handling
 */
public class SafeReader extends FilterReader {

    SafeReader(Reader in) {
        super(in);
    }

    public int read() throws IOException {
        int c = super.read();
        if (c == Parser.EOT) return -1;
        while (Parser.ILLEGAL_CONTROL_CHARS.test(c)) c = super.read();
        return c;
    }

    public void close() throws IOException {
        // do nothing because we don't want to close the underlying stream
    }
}
