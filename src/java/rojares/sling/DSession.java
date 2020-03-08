package rojares.sling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Sling client connects to David server by instantiating a DSession with needed configuration parameters.
 * Then DSession is used to send requests and to receive responses.
 *
 * The client code will look something like this:
 * <pre>
 * DSessionParams params = new DSessionParams()
 *      .setAddress(new InetSocketAddress(hostname, port))
 *      .setUsername(username)
 *      .setPassword(password)
 *      .setTimeout(timeout)
 *      .setMaxTuples(maxtuples)
 * ;
 * DSession session = new DSession(params);
 * DResult result = session.request(deestarInput);
 * ... make as many requests as you want ...
 * session.close()
 * </pre>
 */
public class DSession implements AutoCloseable {

    Logger logger = LoggerFactory.getLogger(DSession.class);

    DSessionParams params;

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    /**
     * <ul>
     *     <li>First socket is opened to the server.</li>
     *     <li>Then the user is authenticated.</li>
     *     <li>Then the DSessionParams are sent to the server and the DSessionParams is bound to the session. This
     *     means that they have a handle to session and when the user changes parameters while the session is connected
     *     those parameter changes are automatically sent to server.</li>
     * </ul>
     * TODO: Add SSL because password is sent in plaintext and nowadays everyone expects it.
     */
    public DSession(DSessionParams params) {

        this.params = params;
        // open socket and streams
        InetSocketAddress serverAddress = new InetSocketAddress(params.getInetAddress(), params.getPort());
        socket = new Socket();
        try {
            // if the server does not answer or end it's response in EOT, then we should not wait forever
            socket.connect(serverAddress, this.params.getTimeout());
            // network protocol is encoded in UTF-8
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException ioe) {
            throw new SlingException("Unable to open socket to " + serverAddress.toString(), ioe);
        }

        // authenticate
        try {
            /*
             After the connection has been opened the server waits for authentication.
             username:...<NEWLINE>
             password:...<NEWLINE>
             <EOT>
             */
            Sling.checkForControlCharacters(params.getUsername());
            Sling.checkForControlCharacters(params.getPassword());
            String authRequest =
                "username:" + params.getUsername()
                + Sling.C_NEWLINE
                + "password:" + params.getPassword()
                + Sling.C_EOT
            ;
            logger.trace("Sending auth request: {}", Sling.formatCtrlChars(authRequest));
            out.print(authRequest);
            out.flush();

            /*
             We let the DResponse parse the response from server.
             In case of successful authentication the DResult is empty
             Only a failed authentication will return a DavidException (or SlingException in case of unusual errors)
             */
            DResponse response = new DResponse(in, this.params);

            // bind DSessionParams to this open session
            params.bind(this);
        }
        catch (SlingException se) {
            close();
            throw se;
        }
    }

    /**
     * All interaction with David server is done by sending a request and resceiving a response. A request-response pair
     * is called interaction. Accepted deestarInput is documented as part of the David project. Currently David accepts
     * only characters from Unicode BMP so surrogate characters will cause David to throw an exception. Also the first
     * 32 characters, called control characters (with the exception of TAB, CR and LF) are not allowed in deestarInput.
     * Session has one socket and one can run only one interaction at a time. Therefore this method is synchronized.
     */
    public synchronized DResult request(String deestarInput) {
        if (out == null) throw new SlingException("Session is closed.");
        Sling.checkForControlCharactersExcept3(deestarInput);
        String request = deestarInput + Sling.C_EOT;
        logger.trace("Sending request: {}", Sling.formatCtrlChars(request));
        out.print(request);
        return new DResponse(this.in, this.params).getDResult();
    }

    /**
     * Closes the socket and related streams. Also unbinds the parameters from the session.
     * Throws SlingException if this resource cannot be closed. However I can not imagine why closing would not be
     * possible. The socket session surely can be just broken/discarded. The server-side will have a timeout that
     * will auto-close the session on it's end if it hasn't heard from the client.
     */
    @Override
    public void close() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (params != null) params.unbind();
        }
        catch (SlingException se) {throw se;}
        catch (Exception ex) { throw new SlingException("Problem closing the session", ex); }
        finally {
            logger.info("DSession is closed.");
            out = null;
            in = null;
            socket = null;
        }
    }
}
