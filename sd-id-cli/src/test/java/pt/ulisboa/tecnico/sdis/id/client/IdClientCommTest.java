package pt.ulisboa.tecnico.sdis.id.client;

import javax.xml.registry.JAXRException;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.uddi.UDDINaming;

public class IdClientCommTest {

    IdClient client = null;

    @BeforeClass
    public static void oneTimeSetUp() {
    }

    @AfterClass
    public static void oneTimeTearDown() {
    }

    @Before
    public void setUp() {
        client = null;
    }

    @After
    public void tearDown() {
        client = null;
    }

    /**
     * In this test the server is mocked to
     * simulate a communication exception.
     * 
     * @throws JAXRException
     * @throws serviceFindException
     */
    @Test(expected = serviceFindException.class)
    public void testMockServerException(@Mocked final UDDINaming uddi) throws JAXRException, serviceFindException {
        new Expectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                result = new serviceFindException();
            }
        };

        client = new IdClient("some.url", "Some-service-name");
    }

    /*
    @Test
    public <M extends SDId & BindingProvider & Closeable> void success(@Mocked final UDDINaming uddi,
            @Mocked final SDId_Service service, @Mocked final M port) throws JAXRException, serviceFindException,
            EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {

        new NonStrictExpectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                result = "http://localhost:8080";
                new SDId_Service();
                service.getSDIdImplPort();
                result = port;
                port.getRequestContext();
                port.createUser(anyString, anyString);
            }
        };

        client = new IdClient("http://localhost:8080", "Some-service-name");

        client.createUser("Someuser", "ajhgdjhg@jsh.com");

    }
    */
}