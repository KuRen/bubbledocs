package pt.ulisboa.tecnico.sdis.store.ws.client;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

/**
 * Test suite
 */
public class StoreTest {

    // static members

    private static SDStore port;
    private static String userId = "StoreTest";
    private static String documentId = "StoreTestDoc";
    private static byte[] content;
    private static String contentString = "StoreTestDoc Content";
    private static int testNumber;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
    	port = new StoreClient("http://localhost:8081", "sd-store");
        content = contentString.getBytes();
        testNumber = 0;
    }

    @AfterClass
    public static void oneTimeTearDown() {
        port = null;
    }

    // members

    private static DocUserPair pair;

    // initialization and clean-up for each test

    @Before
    public void setUp() {
        pair = new DocUserPair();
        pair.setDocumentId(documentId + testNumber);
        pair.setUserId(userId + testNumber);
    }

    @After
    public void tearDown() {
        testNumber++;
        pair = null;
    }

    // Success
    @Test
    public void testStore() throws Exception {
        port.createDoc(pair);
        port.store(pair, content);
    }

    // Fail - Unknown UserId
    @Test(expected = UserDoesNotExist_Exception.class)
    public void testStoreUnknownUser() throws Exception {
        port.store(pair, content);
    }

    // Fail - Unknown DocumentId
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testStoreDocUnknownDoc() throws Exception {
        port.createDoc(pair);
        pair.setDocumentId("fail");
        port.store(pair, content);
    }
    
 // Fail - Capacity Exceeded
    @Test(expected = CapacityExceeded_Exception.class)
    public void testStoreCapacityExceeded() throws Exception {
        port.createDoc(pair);
        port.store(pair, new byte[1024*10+1]);
    }
    
 // Fail - Capacity Exceeded
    @Test(expected = CapacityExceeded_Exception.class)
    public void testStoreCapacityExceededTwo() throws Exception {
        port.createDoc(pair);
        try {
        port.store(pair, content);
        } catch(CapacityExceeded_Exception ce) {
            fail();
        }
        pair.setDocumentId("fail");
        port.createDoc(pair);
        port.store(pair, new byte[1024*10+1]);
    }

}
