package rojares.sling;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DSessionTest {

    DServer server;
    DSession clientSession;

    @Before
    public void setUp() {
        server = new DServer();
        clientSession = new DSession();
    }

    @Test
    @Order(1)
    public connect() {
        clientSession.connect(
            new InetSocketAddress(InetAddress.getLoopbackAddress(), 3434),
            "testuser",
            "password",
            new SlingParams().setMaxTuples(100)
        );
    }

    @Before
    public void tearDown() {
        server.close();
        clientSession.close();
    }

}
