package rojares.sling.parser;

import rojares.sling.SlingException;

import java.io.Reader;

/**
 * Reads an identifier and equal sign ignoring whitespace
 * 1. possible whitespace before identifier (EOT here does not produce exception but finishes the response)
 * 2. identifier
 * 3. possible whitespace after identifier
 * 4. equal sign
 * If illegal characters or EOT are found throws exception
 * Once equal sign has been read, stores the identifier into context
 */
public class IdentifierParser {

    Reader reader;
    ParsingContext ctx;
    StringBuilder buffer = new StringBuilder();

    int BEFORE = 1;
    int ON = 2;
    int AFTER = 3;
    int state = BEFORE;

    IdentifierParser(Reader reader, ParsingContext ctx) {
        this.reader = reader;
        this.ctx = ctx;
    }

    boolean parse() {

        int c;
        while ( c = reader.read()) {

            if (state == BEFORE) {
                if (c == -1) return true;
                else if (Character.isWhitespace(c)) continue;
                else if (ResponseParser.ASCII_OR_UNDERSCORE.test(c)) {
                    buffer.append((char) c);
                    state = ON;
                    continue;
                } else throw new SlingException("...");
            }
            else if (state == ON) {
                if (Character.isWhitespace(c) || ResponseParser.EQUAL.test(c)) {
                    ctx.setIdentifier(buffer.toString());
                    buffer.delete(0, buffer.length());
                    if (Character.isWhitespace(c)) {
                        state = AFTER;
                        continue;
                    }
                    else if (ResponseParser.EQUAL.test(c)) {
                        state = BEFORE;
                        return false;
                    }
                } else if (ResponseParser.ASCII_OR_UNDERSCORE_OR_DIGIT.test(c)) {
                    buffer.append((char) c);
                }
                else throw new SlingException("...");
            } else { // state == AFTER
                if (Character.isWhitespace(c)) continue;
                else if (ResponseParser.EQUAL.test(c)) {
                    state = BEFORE;
                    return false;
                } else throw new SlingException("...");
            }
        }
    }
}
