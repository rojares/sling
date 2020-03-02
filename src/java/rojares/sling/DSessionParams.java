package rojares.sling;

import java.net.InetSocketAddress;

/**
 * Class defining the recognized parameters of sling needed for connecting, authenticating and other configuration of
 * the session.
 */
public class DSessionParams {

    // DSessionParams is bound to a session so that when a server parameter is updated that update is sent to server
    // right away
    private DSession boundSession;
    void bind(DSession session) {
        if (this.boundSession != null) {
            throw new SlingException("This DSessionParams is already bound to another session");
        }
        this.boundSession = session;
        session.request(allServerParams());
    }
    private String allServerParams() {
        return maxResponseSizeCommand() + maxTuplesCommand();
    }

    public void unbind() {
        this.boundSession = null;
    }
    /* Connection params */

    private InetSocketAddress serverAddress;
    private String username;
    private String password;
    public DSessionParams setAddress(InetSocketAddress serverAddress) {
        this.serverAddress = serverAddress;
        return this;
    }
    public DSessionParams setUsername(String username) {
        this.username = username;
        return this;
    }
    public DSessionParams setPassword(String password) {
        this.password = password;
        return this;
    }

    /* Client params */

    // timeout waiting for response
    private int timeout = 5_000;
    public int getTimeout() {
        return timeout;
    }
    public DSessionParams setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }


    /* Server params */

    /*
    We set a limit to how large a response can be. This limit is expressed in characters (codepoints) in BMP.
    When encoded in utf-8 they take 1-3 bytes but when stored into memory they take 2 bytes.
    So the result will consume approximately 2 * limit of bytes in memory
    The default is 20 million characters which will consume at least 40MB of memory.
     */
    private int maxResponseSize = 20_000_000;
    public int getMaxResponseSize() {
        return maxResponseSize;
    }
    public DSessionParams setMaxResponseSize(int maxResponseSize) {
        if (maxResponseSize <= 0) maxResponseSize = Integer.MAX_VALUE;
        this.maxResponseSize = maxResponseSize;
        if (boundSession != null) boundSession.request(maxResponseSizeCommand());
        return this;
    }
    private String maxResponseSizeCommand() {
        return "admin(max_response_size, " + this.maxResponseSize + ");";
    }

    // 0 means all tuples, clearly to maximum limit is Integer.MAX_VALUE
    private int maxTuples = 1000;
    public DSessionParams setMaxTuples(int maxTuples) {
        if (maxTuples < 0) maxTuples = 0;
        this.maxTuples = maxTuples;
        if (boundSession != null) boundSession.request(maxTuplesCommand());
        return this;
    }
    public int getMaxTuples() {
        return maxTuples;
    }
    private String maxTuplesCommand() {
        return "admin(max_tuples, " + this.maxTuples + ");";
    }
}
