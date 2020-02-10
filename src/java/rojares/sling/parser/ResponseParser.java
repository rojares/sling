package rojares.sling.parser;

import rojares.sling.*;
import rojares.sling.type.DValue;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntPredicate;

/**
 * This is very close to json but I like to do it myself to keep total control. Also I use now control characters so
 * that I can avoid more complex parsing, delimiters and escapes
 *
 * DResult := name_value_list
 * name_value_list := name_value[FS]name_value[FS]...
 * name_value := identifier=literal
 * identifier := [a-zA-Z_][a-zA-Z_0-9]*
 * literal := primitive_literal | collection_literal
 *
 * primitive_literal := B:boolean_literal | I:integer_literal | S:string_literal
 * boolean_literal := B:(TRUE|FALSE|NULL) (case-insensitive)
 * integer_literal := I:(-?[0-9]+|NULL) (null is case-insensitive)
 * string_literal := S:(>sequence_of_zero_or_more_unicode_characters|NULL) (null is case-insensitive)
 * primitive_type := B|I|S
 *
 * collection_literal := table_literal
 * table_literal :=
 * T:HEADER
 *     primitive_type:identifier[US]primitive_type:identifier[US]...
 * [GS]
 * BODY
 *     primitive_literal[US]primitive_literal[US]...
 *     [RS]
 *     primitive_literal[US]primitive_literal[US]...
 *     ...
 * [GS]
 */
public class ResponseParser {

    final static IntPredicate ILLEGAL_CONTROL_CHARS = n ->
        n < 4  || // allow EOT
        (n >= 5 && n < 9) || // allow HT and LF
        (n >= 11 && n < 28) || // allow FS, GS, RS, US
        (n >= 128 && n <= 159)
    ;
    final static IntPredicate ASCII = n ->
        (n >= 65 && n <= 90) ||
        (n >= 97 && n <= 122)
    ;
    final static IntPredicate ASCII_OR_UNDERSCORE = ASCII.or(n -> n == 95);
    final static IntPredicate DIGIT = n -> n >= 48 && n <= 57;
    final static IntPredicate ASCII_OR_UNDERSCORE_OR_DIGIT = ASCII_OR_UNDERSCORE.or(DIGIT);
    final static IntPredicate EQUAL = n -> n == 61;

    final static int FS = 28, GS = 29, RS = 30, US = 31;

    public static Map<String, DValue> parse(BufferedReader reader) throws SlingException {
        SafeReader safeReader = new SafeReader(reader);
        ParsingContext ctx = new ParsingContext();
        IdentifierParser iParser = new IdentifierParser(safeReader, ctx);
        DValueParser vParser = new DValueParser(safeReader, ctx);
        while(true) {
            if (iParser.parse()) break;
            if (vParser.parse()) break;
        }
        return ctx.getResult();
    }

}