package rojares.sling;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Sling client connects to David server by instantiating a DSession with needed configuration parameters and then
 * calling connect(). All the configurations parameters are in the DSessionParams. The connect() method takes only
 * the InetSocketAddress and authentication credentials that are set to null when closing. Therefore it is possible
 * to reconnect using the same DSession object, however there is no clear performance difference to be gained.
 *
 * So the client code will look something like this:
 * DSessionParams params = new DSessionParams(...);
 * DSession session = new DSession(params);
 * session.connect(new InetSocketAddress(hostname, port), username, password);
 * DResult result = session.request(deestarInput);
 * ... make as many requests as you want ...
 * session.close()
 */
public class DSession implements AutoCloseable {

    DSessionParams params;

    public DSession(DSessionParams params) {
        this.params = params;
    }

    /**
     * First socket is opened to the server.
     * Then the user is authenticated.
     * Then the DSessionParams are sent to the server and the DSessionParams is bound to the
     * session. This means that they have a handle to session and when the user changes parameters while the session
     * is connected those parameter changes are automatically sent to server.
     *
     * TODO: Add SSL because password is sent in plaintext and nowadays everyone expects it.
     */
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    public synchronized void connect(InetSocketAddress serverAddress, String username, String password) {

        // open socket and streams
        socket = new Socket();
        try {
            // if the server does not answer or end it's response in EOT, then we should not wait forever
            socket.connect(serverAddress, Sling.RESPONSE_TIMEOUT);
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
            Sling.checkForControlCharacters(username);
            Sling.checkForControlCharacters(password);
            out.print(
                "username:" + username
                + Sling.C_NEWLINE
                + "password:" + password
                + Sling.C_EOT
            );
            out.flush();

            /*
             We let the DResponse parse the response from server.
             In case of successful authentication the DResult is empty
             Only a failed authentication will return a DavidException
             */
            DResponse response = new DResponse().readResponse(in);
            if (response.isError()) throw new response.getDError();

            // let's send the initial params. In case of successful configuration the result is empty
            out.print(this.params.getRequestString());
            response.readResponse(in);
            if (response.isError()) throw new response.getException();
            // bind DSessionParams to this open session
            params.bind(this);
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
        Sling.checkForControlCharacters(deestarInput);
        out.print(deestarInput + Sling.C_EOT);
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
            out = null;
            in = null;
            socket = null;
        }
    }
}
