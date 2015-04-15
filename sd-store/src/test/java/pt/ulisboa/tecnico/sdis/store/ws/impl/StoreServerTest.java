package pt.ulisboa.tecnico.sdis.store.ws.impl;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.*;

import pt.ulisboa.tecnico.sdis.store.ws.impl.uddi.UDDINaming;

/**
 *  Test suite
 */
public class StoreServerTest {

    // static members
	
    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    // members
    
    StoreServer server;
    @Mocked
    Endpoint endpoint;
    @Mocked
    UDDINaming uddi;
    
    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	server = new StoreServer("http://localhost:8081", "sd-store", "http://localhost:8080/store-ws/endpoint");
    }

    @After
    public void tearDown() {
    	server = null;
    }
    
    @Test(expected = JAXRException.class)
    public void testJAXRException() throws JAXRException {
        new Expectations() {
        	{
        		endpoint.publish(anyString);
        		uddi.rebind(anyString, anyString);
        		result = new JAXRException();
        	}
        };
        
        server.run();
    }
    
    @Test
    public void testStoreServer() throws JAXRException {

        server.run();
        server.stop();
    }
    
}
