package rojares.sling;

import java.util.regex.Pattern;

/**
 * I can't decide if I should make this runtime or not. DavidException represents different exceptions that occurred
 * while executing the interaction input on the serverside. These exceptions are often transferred all the way to the
 * user. Therefore they have more structure so that they can be localized and properly shown to the user.
 */
public class DavidException extends SlingException {

    /*
    errorCodes follow hierarchical dot notation, are documented in David documentation and can be expanded by the
    user. Example: SYNTAX.OPERATOR.<OPERATOR_NAME>.23 or CONSTRAINT.USER.<CONSTRAINT_NAME>.3. The syntax is not
    clear yet at the time of the writing.
     */
    String errorCode;

    /*
    Whenever a David error is defined it must contain a textual message in english language. This is a fallback
    mechanism that can be used when these exceptions are not shown to user or only developers.
    This message can be localized or customized by using errorCode as a key to retrieve another message in different
    language or with more user friendly explanation.
     */
    String englishMessage;

    /*
    When error occurs it is important to show to user what part of the interaction input caused the exception. When
    the input is very large then only some lines before and after the exception causing area is included for context.
    The linecount of how much context should be shown is configurable by the client. Also the start and end positions
     of the exception causing input are provided so that the client can present the exception causing input nicely.
     */
    String exceptionCausingInput;
    // the start counted from the beginning of exceptionCausingInput
    int exceptionStart;
    // this is the most crucial position that caused the exception and must be between (inclusive) start and end
    int exceptionPoint;
    // the end counted from the beginning of exceptionCausingInput
    int exceptionEnd;

    public DavidException(
        String errorCode,
        String englishMessage,
        String exceptionCausingInput,
        int exceptionStart,
        int exceptionPoint,
        int exceptionEnd
    ) {
        // englishMessage is set as exception message
        super(englishMessage);
        this.errorCode = errorCode;
        this.englishMessage = englishMessage;
        this.exceptionCausingInput = exceptionCausingInput;
        this.exceptionStart = exceptionStart;
        this.exceptionPoint = exceptionPoint;
        this.exceptionEnd = exceptionEnd;
    }

    /**
     * errorcode[FS]englishMessage[FS]exceptionCausingInput[FS]exceptionStart[FS]exceptionPoint[FS]exceptionEnd
     */
    public static DavidException parse(StringBuilder errorResponse) {
        String[] errorComponents = DResult.FS_Pattern.split(errorResponse);
        return new DavidException(
            errorComponents[0],
            errorComponents[1],
            errorComponents[2],
            Integer.parseInt(errorComponents[3]),
            Integer.parseInt(errorComponents[4]),
            Integer.parseInt(errorComponents[5])
        );
    }
}
