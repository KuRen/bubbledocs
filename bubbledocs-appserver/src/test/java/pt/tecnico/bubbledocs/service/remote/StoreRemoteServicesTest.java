package pt.tecnico.bubbledocs.service.remote;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.CannotStoreDocumentException;

@Ignore("Takes too much time to test always and it's tested on system testing")
public class StoreRemoteServicesTest extends SdRemoteServicesTest {
    // static members

    private static StoreRemoteServices service;
    private static String documentId = "LoadTestDoc";
    private static byte[] content;
    private static String contentString = "LoadTestDoc Content";
    private static int testNumber;
    private static final String USER = "alice";
    private String doc = null;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() throws Exception {
        service = new StoreRemoteServices();
        content = contentString.getBytes();
        testNumber = 0;
    }

    @AfterClass
    public static void oneTimeTearDown() {
        service = null;
    }

    // initialization and clean-up for each test

    @Override
    @Before
    public void setUp() {
        doc = documentId + testNumber;
    }

    @Override
    @After
    public void tearDown() {
        testNumber++;
    }

    // Success
    // Assuming pre-loaded data on server
    @Test
    public void testLoad() throws Exception {
        service.storeDocument(USER, doc, content);
        byte[] result = service.loadDocument(USER, doc);
        Assert.assertArrayEquals(content, result);
    }

    // Fail - Unknown UserId
    @Test(expected = CannotLoadDocumentException.class)
    public void testLoadUnknownUser() throws Exception {
        service.loadDocument(USER, doc);
    }

    // Success
    // Assuming pre-loaded data on server
    @Test
    public void testStore() throws Exception {
        service.storeDocument(USER, doc, content);
    }

    // Fail - Unknown UserId
    @Test(expected = CannotStoreDocumentException.class)
    public void testStoreUnknownUser() throws Exception {
        service.storeDocument("Unknon", doc, content);;
    }

    // Fail - Capacity Exceeded
    @Test(expected = CannotStoreDocumentException.class)
    public void testStoreCapacityExceeded() throws Exception {
        service.storeDocument(USER, doc, new byte[1024 * 100 + 1]);
    }
}
