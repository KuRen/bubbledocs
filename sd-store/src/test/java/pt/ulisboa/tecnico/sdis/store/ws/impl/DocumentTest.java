package pt.ulisboa.tecnico.sdis.store.ws.impl;

import org.junit.*;

/**
 *  Test suite
 */
public class DocumentTest {

    // members

    private Document doc;
    private byte[] content = new String("Conteudo de teste!").getBytes();

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	doc = new Document();
    	doc.setContent(content);
    }

    @After
    public void tearDown() {
        doc = null;
    }

    @Test
    public void testGetContent() {
        Assert.assertArrayEquals(content, doc.getContent());
    }
    
    @Test
    public void testSetContent() {
        doc.setContent(new String("Must be the same").getBytes());
        Assert.assertArrayEquals(new String("Must be the same").getBytes(), doc.getContent());
    }
}
