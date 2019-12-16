package rojares.sling;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Sling client connects to David server by instantiating a DSession with needed configuration parameters and then
 * calling connect(). The socket connection remains alive until user explicitly closes it or it exits a try block
 * that auto-closes it.
 * I don't see any reason why the session couldn't be opened and closed multiple times.
 */
public class DSession implements AutoCloseable {

    // EOT = END OF TRANSMISSION, this is used to signal to server that the request is over and the client is waiting
    // for response
    static String EOT = "\u0004";
    // Sling wire protocol recognizes only LF as a newline character. CR is always removed when normalising input.
    static String NEWLINE = "\n";

    SlingParams params;

    public DSession(SlingParams params) {
        this.params = params;
    }

    /**
     * Reads params and uses that info to create the connection, authenticate and configure the session.
     * TODO: Add SSL because password is sent in plaintext and nowadays everyone expects it.
     */
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    public synchronized SlingParams connect(InetSocketAddress serverAddress, String username, String password) throws SlingException {

        // create socket and streams
        socket = new Socket();
        try {
            // if the server does not answer in 5 seconds then something is seriously wrong.
            socket.connect(serverAddress, 5000);
            // network protocol is encoded in UTF-8
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            in = new BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        }
        catch (IOException ioe) {
            throw new SlingException(ioe);
        }

        try {
            // authenticate
            out.print(
                "username:" + params.getUsername()
                + NEWLINE
                + "password:" + params.getPassword()
                + EOT
            );
            out.flush();

            // successful response for authentication is empty
            DResponse response = new DResponse(in);
            if (response.isError()) throw new response.getException();

            // now let's create slingParams
            this.params = new SlingParams();
            // bind SlingParams to this open session, so that if parameter is changed this change is sent
            // automatically to server
            params.bind(this);
            return params;
        }
        catch (SlingException se) {
            close();
            throw se;
        }
    }

    /**
     * Only one socket per session and therefore it needs to be synchronized.
     */
    public synchronized DResult request(String deestarInput) {
        if (out == null) throw new SlingException("Session is closed.");
        out.print(
                SlingUtils.normalizeInput(deestarInput)
                + EOT
        );
        DResponse response = new DResponse(in);
        if (response.isError()) throw new SlingException("...", DError);
        else return response.getResult();
    }

    /**
     *
     * @throws Exception if this resource cannot be closed. However I can not imagine why closing would not be
     * possible. The socket session surely can be just broken/discarded. The server-side will have a timeout that
     * will auto-close the session on it's end if it hasn't heard from the client.
     */
    @Override
    public void close() throws Exception {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            if (params != null) params.unbind();
        }
        finally {
            out = null;
            in = null;
            socket = null;
        }
    }
}
