package sdstorecli;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;

/**
 * Test suite
 */
public class CreateDocTest {

    // static members

    private static SDStore port;
    private static String userId = "CreateTest";
    private static String documentId = "CreateTestDoc";
    private static int testNumber;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
    	port = new StoreClient("http://localhost:8081", "sd-store");
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
    public void testCreateDoc() throws Exception {
        port.createDoc(pair);
    }

    // Fail - Duplicate document
    @Test(expected = DocAlreadyExists_Exception.class)
    public void testDuplicateDoc() throws Exception {
        port.createDoc(pair);
        port.createDoc(pair);
    }

}
