package rojares.sling;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.DavidServer;

public class DSessionTest {

    @BeforeAll
    static void setup() {
        DavidServer.start();
    }

    DSessionParams params = new DSessionParams();
    DSession clientSession;

    @BeforeEach
    void init() {

    }

    @Test
    void testCredentials() {
        System.out.println("Trying to login with control character in username.");
        params.setUsername("test\u0002");
        params.setPassword("word");
        Throwable exception = assertThrows(DavidException.class, () -> {
            clientSession = new DSession(params);
        });
        System.out.println(exception.getMessage());
    }

    @AfterEach
    void tearDown() {
        clientSession.close();
    }

    @AfterAll
    static void done() {
        DavidServer.stop();
    }

}
