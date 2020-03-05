package rojares.sling;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.DavidServer;

public class DSessionTest {

    @BeforeAll
    static void setup() {
        DavidServer.start();
    }

    DSessionParams params = new DSessionParams().setUsername("test").setPassword("word");
    DSession clientSession;

    @BeforeEach
    void init() {

    }

    @Test
    void testCredentials() {
        System.out.println("Trying to login with control character in username.");
        params.setUsername("test\u0002");
        Throwable exception = assertThrows(SlingException.class, () -> {
            clientSession = new DSession(params);
        });
        System.out.println(exception.getMessage());
        params.setUsername("test");
    }
    /*
    @Test
    void testDTable() {
        clientSession = new DSession(params);
        DResult result = clientSession.request("return (x, /R1);");
        System.out.println(result.getTable("x").toString());
    }
    */
    @AfterEach
    void tearDown() {
        if (clientSession != null) clientSession.close();
    }

    @AfterAll
    static void done() {
        DavidServer.stop();
    }

}
