package sdstorecli;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

/**
 * Test suite
 */
public class LoadTest {

    // static members

    private static SDStore port;
    private static String userId = "LoadTest";
    private static String documentId = "LoadTestDoc";
    private static byte[] content;
    private static String contentString = "LoadTestDoc Content";
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
    public void testLoad() throws Exception {
        port.createDoc(pair);
        port.store(pair, content);
        byte[] result = port.load(pair);
        Assert.assertArrayEquals(content, result);
    }

    // Fail - Unknown UserId
    @Test(expected = UserDoesNotExist_Exception.class)
    public void testLoadUnknownUser() throws Exception {
        port.load(pair);
    }

    // Fail - Unknown DocumentId
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testLoadUnknownDoc() throws Exception {
        port.createDoc(pair);
        pair.setDocumentId("fail");
        port.load(pair);
    }

}
