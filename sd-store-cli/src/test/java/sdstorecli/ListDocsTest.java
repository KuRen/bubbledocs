package sdstorecli;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

/**
 * Test suite
 */
public class ListDocsTest {

    // static members

    private static SDStore port;
    private static String userId = "ListDocsTest";
    private static String documentId = "ListDocsTestDoc";
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
    public void testListDocs() throws Exception {
        port.createDoc(pair);
        List<String> correctList = new ArrayList<String>();
        correctList.add(documentId + testNumber);
        List<String> resultList = port.listDocs(userId + testNumber);
        Assert.assertEquals(correctList, resultList);
    }

    // Fail - Unknown UserId
    @Test(expected = UserDoesNotExist_Exception.class)
    public void testListDocsUnknownUser() throws Exception {
        port.listDocs(userId + testNumber);
    }

}
