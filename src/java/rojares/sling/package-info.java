/**
 * Sling is a client program for accessing David server. Here I document the protocol used to communicate requests and
 * responses between the client and the server.
 * <h2>Server-side</h2>
 * <ol>
 *     <li>Serversocket waits for socket connection on default port 3434</li>
 *     <li>Connection is opened and new socket created</li>
 *     <li>Server waits for authentication (until timeout)</li>
 *     <li>If authentication is successful a new session is created on the server side (else exception is returned and the server waits for a new authentication attempt)</li>
 *     <li>server waits for request, once received executes it and returns result or exception</li>
 *     <li>Step 5 is repeated until the socket connection is closed</li>
 * </ol>
 * <h2>Client-side</h2>
 * <ol>
 *     <li>User created session parameters</li>
 *     <li>User constructs session which opens connection and tries to authenticate and send server parameters</li>
 *     <li>Client sends request and waits for result or exception</li>
 *     <li>Step 3 is repeated until session is closed</li>
 * </ol>
 * <h2>Data formats</h2>
 * D* strings contain unicode BMP characters. However the first 32 characters called control characters are excluded except 3: TAB, CR, LF.
 * This way the control characters can be used to orchestra data transfer over the network.
 * Request and response are ended with End-Of-Transmission (EOT, hex:4) character.
 * Response starts with Acknowledgment (ACK, hex:6) or Negative Acknowledgement (NAK, hex:15) character. After which comes either the result or Exception.
 * The result is determined by the D* input sent by user in the request. It is a sequence of name-value pairs.
 * Here I present the result in ebnf format. The control characters used are:
 * <ul>
 *     <li>File Separator, FS, hex: 1C</li>
 *     <li>Group Separator, GS, hex: 1D</li>
 *     <li>Record Separator, RS, hex: 1E</li>
 *     <li>Unit Separator, US, hex: 1F</li>
 * </ul>
 *
 * <pre>
 * result: name_value? ( '\x1C' name-value )*;
 * name_value: identifier '=' dvalue_literal;
 * identifier: [a-zA-Z_][a-zA-Z_0-9]*;
 * dvalue_literal: primitive_literal | collection_literal;
 *
 * primitive_literal := boolean_literal | integer_literal | string_literal;
 * boolean_literal: 'B:' ( 'TRUE' | 'FALSE' | 'NULL' ); // case-insensitive;
 * integer_literal: 'I:' ( '-'?[0-9]+ | 'NULL' ); // case-insensitive;
 * string_literal: 'S:' ( '&gt;' [bmp_minus_control3]* ) | 'NULL'; // case-insensitive;
 *
 * collection_literal: table_literal;
 * table_literal: header_literal '\x1D' body_literal;
 * header_literal: 'HEADER' attribute_literal? ( '\x1F' attribute_literal )*;
 * attribute_literal: primitive_type ':' identifier;
 * primitive_type: 'B' | 'I' | 'S';
 * body_literal: 'BODY' row_literal? ( '\x1E' row_literal )*;
 * row_literal: primitive_literal? ( '\x1F' primitive_literal )*;
 * </pre>
 *
 * The DavidException that comes after NAK has the following format:
 * <pre>
 * errorcode '\x1C' englishMessage '\x1C' exceptionCausingInput '\x1C' exceptionStart '\x1C' exceptionPoint '\x1C' exceptionEnd
 * </pre>
 *
 * <dl>
 *  <dt>errorcode</dt>
 *  <dd>errorCodes follow hierarchical dot notation. They are documented in David documentation and can be expanded by the user. The syntax is not clear yet at the time of the writing.</dd>
 *  <dt>englishMessage</dt>
 *  <dd>Whenever a David error is defined it must contain a textual message in english language. This is a fallback mechanism that can be used when these exceptions are not shown to user or only developers. This message can be localized or customized by using errorCode as a key to retrieve another message in different language or with more user friendly explanation.</dd>
 *  <dt>exceptionCausingInput</dt>
 *  <dd>When error occurs it is important to show to user what part of the interaction input caused the exception. When the input is very large then only some lines before and after the exception causing area is included for context. The linecount of how much context should be shown is configurable by the client. Also the start and end positions of the exception causing input are provided so that the client can present the exception causing input nicely.</dd>
 *  <dt>exceptionStart</dt>
 *  <dd>The start of the error statement counted from the beginning of exceptionCausingInput</dd>
 *  <dt>exceptionPoint</dt>
 *  <dd>This is the most crucial position that caused the exception and must be between (inclusive) start and end</dd>
 *  <dt>exceptionEnd</dt>
 *  <dd>The end of the error statement counted from the beginning of exceptionCausingInput</dd>
 * </dl>
 */
package rojares.sling;