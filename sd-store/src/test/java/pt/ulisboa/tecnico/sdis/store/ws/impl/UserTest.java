package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;

/**
 *  Test suite
 */
public class UserTest {

    // members

    private User user;
    private byte[] content = new String("Conteudo de teste!").getBytes();
    private String documentId = "TestDocument";
    private int maximumCapacity = 1024 * 10;

    // initialization and clean-up for each test

    @Before
    public void setUp() throws Exception {
    	user = new User();
    }

    @After
    public void tearDown() {
    	user = null;
    }
    
    @Test
    public void testAddDocument() throws Exception {
        user.addDocument(documentId);
    }
    
    @Test(expected = DocAlreadyExists_Exception.class)
    public void testDocAlreadyExists() throws Exception {
        user.addDocument(documentId);
        user.addDocument(documentId);
    }
    
    @Test
    public void testListDocs() throws Exception {
        user.addDocument(documentId);
        user.addDocument(documentId+2);
        List<String> correctList = new ArrayList<String>();
        correctList.add(documentId);
        correctList.add(documentId+2);
        Assert.assertEquals(correctList, user.listDocs());
    }
    
    @Test
    public void testStore() throws Exception {
        user.addDocument(documentId);
        user.store(documentId, content);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testStoreDocDoesNotExist() throws Exception {
        user.addDocument(documentId);
        user.store(documentId+2, content);
    }
    
    @Test(expected = CapacityExceeded_Exception.class)
    public void testCapacityExceeded() throws Exception {
        user.addDocument(documentId);
        byte[] contentFail = new byte[maximumCapacity+1];
        user.store(documentId, contentFail);
    }
    
    @Test
    public void testLoad() throws Exception {
        user.addDocument(documentId);
        user.store(documentId, content);
        Assert.assertArrayEquals(user.load(documentId), content);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testLoadDocDoesNotExist() throws Exception {
        user.addDocument(documentId);
        user.store(documentId, content);
        user.load(documentId+2);
    }
}
