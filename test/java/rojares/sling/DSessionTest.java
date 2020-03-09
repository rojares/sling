package rojares.sling;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.DavidServer;

public class DSessionTest {

    Logger logger = LoggerFactory.getLogger(DSessionTest.class);

    @BeforeAll
    static void setup() {
        DavidServer.start();
    }

    DSessionParams params = new DSessionParams().setUsername("test").setPassword("word");

    @Test
    void openCloseOpenClose() {
        logger.info("Trying to open and close the socket 2 times.");
        logger.info("Opening 1st socket...");
        DSession clientSession = new DSession(params);
        logger.info("Closing 1st socket...");
        clientSession.close();
        logger.info("Opening 2nd socket...");
        clientSession = new DSession(params);
        logger.info("Closing 2nd socket...");
        clientSession.close();
    }

    @Test
    void testErroneousCredentials() {
        logger.info("Trying to login with control character in username.");
        params.setUsername("atest\u0002");
        Throwable exception = assertThrows(SlingException.class, () -> {
            DSession clientSession = new DSession(params);
        });
        logger.info(exception.getMessage());
        params.setUsername("test");
    }

    @Test
    void testDTable() {
        DSession clientSession = new DSession(params);
        DResult result = clientSession.request("return (x, /R1);");
        //System.out.println(result.getTable("x").toString());
    }

    @AfterAll
    static void done() {
        DavidServer.stop();
    }

}
