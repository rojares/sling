package rojares.sling;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.DavidServer;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;


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
    void testBasicQueries() {

        DSession clientSession = new DSession(params);

        Map<String, String> queryBatch = new ListOrderedMap();
        queryBatch.put("relation", "/R1");
        queryBatch.put("where1", "where(/R1, a=99 and b=true)");
        queryBatch.put("where2", "where(/R1, a<99 and b=true)");
        queryBatch.put("project", "project(/R1, {a,b})");
        queryBatch.put("project_where", "project(where(/R1, a=99), {a,b})");
        queryBatch.put("where_project", "where(project(/R1, {a,b}), a=99)");
        queryBatch.put("insert", "insert(/R1, (a:1, b:false,c:\"Taras\"))");
        queryBatch.put("update1", "update(/R1, {a=100})");
        queryBatch.put("update2", "update(/R1, {a=100}, a=99 and b= true)");
        queryBatch.put("delete1", "delete(/R1, a=100)");
        queryBatch.put("delete2", "delete(/R1)");

        DResult result;
        for (Map.Entry<String, String> entry : queryBatch.entrySet()) {
            String query = "return (" + entry.getKey() + ", " + entry.getValue() + ");";
            logger.info("Running query: {}", query);
            result = clientSession.request(query);
            System.out.println(result.toString());
        }
    }

    @AfterAll
    static void done() {
        DavidServer.stop();
    }

}
