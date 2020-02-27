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
        return maxTuplesCommand();
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

    // 0 means all tuples, clearly to maximum limit is Integer.MAX_VALUE
    private int maxTuples = 1000;
    public DSessionParams setMaxTuples(int maxTuples) {
        if (maxTuples < 0) maxTuples = 0;
        if (boundSession != null) boundSession.request(maxTuplesCommand());
        this.maxTuples = maxTuples;
        return this;
    }
    public int getMaxTuples() {
        return maxTuples;
    }
    private String maxTuplesCommand() {
        return "admin(max_tuples, " + this.maxTuples + ");";
    }
}
