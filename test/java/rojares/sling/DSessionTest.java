package rojares.sling;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.apache.commons.collections4.map.ListOrderedMap;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

//    @Test
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

    
    // @Test
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

        Map<String, String> queryBatch = new ListOrderedMap<>();
        
        // TODO: relation is reserved keyword in our grammar, thus cannot be used anywhere else except relation literal
        queryBatch.put("relation_1", "/R1");
        queryBatch.put("where1", "where(/R1, a=99 and b=true)");
        
        // FIXME;
        // Fails because this query returns empty body as there are no values of attribute a that are < 99
        // TRACE rojares.sling.typed_value.collection.DTable - DTable literal: HEADER I:a[US]B:b[US]S:c[US]I:id[GS]BODY
        // queryBatch.put("where2", "where(/R1, a<99 and b=true)");
   
        // TODO: project is reserved keyword in our grammar, thus cannot be used anywhere else except relation operator statement
        queryBatch.put("project_1", "project(/R1, {a,b})");
        
        queryBatch.put("project_where", "project(where(/R1, a=99), {a,b})");
        
// For Taras FIXME: where(project(/R1, {a,b}), a=99)
//        queryBatch.put("where_project", "where(project(/R1, {a,b}), a=99)");

        // TODO: insert is reserved keyword in our grammar, thus cannot be used anywhere else except insert data statement 
        queryBatch.put("insert_1", "insert(/R1, (a:1, b:false,c:\"Taras\"))");
        
        queryBatch.put("update1", "update(/R1, {a=100})");

        // FIXME;
        // Fails because this query returns empty body as /R1 at this point does not have values of attributes a = 99
        // 
        // TRACE rojares.sling.typed_value.collection.DTable - DTable literal: HEADER I:a[US]B:b[US]S:c[US]I:id[GS]BODY 
//      queryBatch.put("update2", "update(/R1, {a=100}, a=99 and b=true)");
        
        queryBatch.put("delete1", "delete(/R1, a=100)");
        
        // FIXME;
        // Fails because this query returns empty body as /R1 at this point does not have tuples, they were deleted by previous delete statement
        //         
        // TRACE rojares.sling.typed_value.collection.DTable - DTable literal: HEADER I:a[US]B:b[US]S:c[US]I:id[GS]BODY
        // queryBatch.put("delete2", "delete(/R1)");
        
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
