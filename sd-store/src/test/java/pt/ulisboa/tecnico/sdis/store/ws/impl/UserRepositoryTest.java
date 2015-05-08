package pt.ulisboa.tecnico.sdis.store.ws.impl;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;

/**
 *  Test suite
 */
public class UserRepositoryTest {

    // members

    private UserRepository repository;
    private byte[] content = new String("Conteudo de teste!").getBytes();
    private String documentId = "TestDocument";
    private int maximumCapacity = 1024 * 10;

    // initialization and clean-up for each test

    @Before
    public void setUp() throws Exception {
    	repository = new UserRepository();
    }

    @After
    public void tearDown() {
    	repository = null;
    }
    
    @Test
    public void testAddDocument() throws Exception {
        repository.addDocument(documentId);
    }
    
    @Test(expected = DocAlreadyExists_Exception.class)
    public void testDocAlreadyExists() throws Exception {
        repository.addDocument(documentId);
        repository.addDocument(documentId);
    }
    
    @Test
    public void testListDocs() throws Exception {
        repository.addDocument(documentId);
        repository.addDocument(documentId+2);
        List<String> correctList = new ArrayList<String>();
        correctList.add(documentId);
        correctList.add(documentId+2);
        Assert.assertEquals(correctList, repository.listDocs());
    }
    
    @Test
    public void testStore() throws Exception {
        repository.addDocument(documentId);
        repository.store(documentId, content);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testStoreDocDoesNotExist() throws Exception {
        repository.addDocument(documentId);
        repository.store(documentId+2, content);
    }
    
    @Ignore
    @Test(expected = CapacityExceeded_Exception.class)
    public void testCapacityExceeded() throws Exception {
        repository.addDocument(documentId);
        byte[] contentFail = new byte[maximumCapacity+1];
        repository.store(documentId, contentFail);
    }
    
    @Ignore
    @Test(expected = CapacityExceeded_Exception.class)
    public void testCapacityExceededTwo() throws Exception {
        repository.addDocument(documentId);
        repository.addDocument(documentId+2);
        try {
            repository.store(documentId, content);
        } catch(CapacityExceeded_Exception ce) {
            fail();
        }
        byte[] contentFail = new byte[maximumCapacity];
        repository.store(documentId+2, contentFail);
    }
    
    @Test
    public void testLoad() throws Exception {
        repository.addDocument(documentId);
        repository.store(documentId, content);
        Assert.assertArrayEquals(repository.load(documentId), content);
    }
    
    @Test(expected = DocDoesNotExist_Exception.class)
    public void testLoadDocDoesNotExist() throws Exception {
        repository.addDocument(documentId);
        repository.store(documentId, content);
        repository.load(documentId+2);
    }
}
