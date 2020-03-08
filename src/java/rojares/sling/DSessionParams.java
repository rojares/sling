package rojares.sling;

import java.net.InetAddress;

/**
 * Class defining the recognized parameters of sling needed for connecting, authenticating and other configuration of
 * the session. The parameters can be classified into 3 different categories:
 * <dl>
 *     <dt>Connection parameters</dt>
 *     <dd>Parameters needed when creating the session</dd>
 *     <dt>Clientside parameters</dt>
 *     <dd>Parameters that affect only the client</dd>
 *     <dt>Serverside parameters</dt>
 *     <dd>Parameters used to configure the session on the server</dd>
 * </dl>
 */
public class DSessionParams {

    /**
     * Just a default constructor
     */
    public DSessionParams() {}

    /*
     DSessionParams is bound to a session so that when a server parameter is updated that update is sent to server
     right away.
     */
    private DSession boundSession;
    void bind(DSession session) {
        if (maxTuples == defaultMaxTuples)
        if (this.boundSession != null) {
            throw new SlingException("This DSessionParams is already bound to another session");
        }
        this.boundSession = session;
        String initParams = getInitialNonDefaultParams();
        if (initParams.length() > 0) session.request(initParams);
    }
    private String getInitialNonDefaultParams() {
        String s = "";
        if (maxResponseSize != defaultMaxResponseSize) s += maxResponseSizeCommand();
        if (maxTuples != defaultMaxTuples) s += maxTuplesCommand();
        return s;
    }

    void unbind() {
        this.boundSession = null;
    }


    /* CONNECTION PARAMS */


    private InetAddress inetAddress = InetAddress.getLoopbackAddress();
    /**
     * Connection parameter
     * @param inetAddress I use this class because it is a well developed class to point to a host
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
        return this;
    }
    public InetAddress getInetAddress() {
        return this.inetAddress;
    }

    private int port = 3434; // this is the default port of David
    /**
     * Connection parameter. Default port is 3434
     * @param port where the server is waiting for connections
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setPort(int port) {
        this.port = port;
        return this;
    }
    public int getPort() {
        return this.port;
    }

    private String username;
    /**
     * Connection parameter.
     * @param username control characters are not allowed
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setUsername(String username) {
        this.username = username;
        return this;
    }
    public String getUsername() {
        return this.username;
    }


    private String password;
    /**
     * Connection parameter.
     * @param password control characters are not allowed
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setPassword(String password) {
        this.password = password;
        return this;
    }
    public String getPassword() {
        return this.password;
    }


    /* CLIENT PARAMS */


    // timeout waiting for response
    private int timeout = 5_000;
    /**
     * Client parameter.
     * @param timeout waiting for response from the server
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }
    public int getTimeout() {
        return timeout;
    }


    /* SERVER PARAMS */


    private int defaultMaxResponseSize = 20_000_000;
    private int maxResponseSize = defaultMaxResponseSize;
    /**
     * Server parameter.<br>
     * We set a limit to how large a response can be. This limit is expressed in characters (codepoints) in BMP.
     * When encoded in utf-8 they take 1-3 bytes but when stored into memory they take 2 bytes.
     * So the result will consume approximately 2 * limit of bytes in memory
     * The default is 20 million characters which will consume at least 40MB of memory.
     * @param maxResponseSize in characters
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setMaxResponseSize(int maxResponseSize) {
        if (maxResponseSize <= 0) maxResponseSize = Integer.MAX_VALUE;
        if (this.maxResponseSize == maxResponseSize) return this;
        // real change happened
        this.maxResponseSize = maxResponseSize;
        if (boundSession != null) boundSession.request(maxResponseSizeCommand());
        return this;
    }
    private String maxResponseSizeCommand() {
        return "admin(max_response_size, " + this.maxResponseSize + ");";
    }
    public int getMaxResponseSize() {
        return maxResponseSize;
    }

    private int defaultMaxTuples = 1000;
    private int maxTuples = defaultMaxTuples;
    /**
     * Server parameter.<br>
     * 0 means all tuples, the maximum limit is Integer.MAX_VALUE
     * @param maxTuples that a single DTable can hold
     * @return DSessionParams is returned in order to implement the Builder pattern
     */
    public DSessionParams setMaxTuples(int maxTuples) {
        if (maxTuples < 0) maxTuples = 0;
        if (this.maxTuples == maxTuples) return this;
        // real change happened
        this.maxTuples = maxTuples;
        if (boundSession != null) boundSession.request(maxTuplesCommand());
        return this;
    }
    private String maxTuplesCommand() {
        return "admin(max_tuples, " + this.maxTuples + ");";
    }
    public int getMaxTuples() {
        return maxTuples;
    }

}
