package pt.ulisboa.tecnico.sdis.id.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.xml.registry.JAXRException;
import javax.xml.ws.WebServiceException;

import mockit.Expectations;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;

public class IdClientCommTest {

    IdClient client = null;
    private static int x = 1;

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
    public void testMockServerCommunicationException(@Mocked final UDDINaming uddi) throws JAXRException, serviceFindException {
        new Expectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                result = new serviceFindException();
            }
        };

        client = new IdClient("some.url", "Some-service-name");
    }

    @Test(expected = UserAlreadyExists_Exception.class)
    public <T extends SDId> void testMockServerException(@Mocked final UDDINaming uddi) throws Exception {

        new MockUp<T>() {
            @Mock
            void createUser(String name, String email) throws UserAlreadyExists_Exception {
                throw new UserAlreadyExists_Exception("fabricated", null);
            }
        };

        new NonStrictExpectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                result = "http://localhost:8081";
            }
        };

        // Unit under test is exercised.
        IdClient client = new IdClient("http://localhost:8081", "sd-id");
        client.createUser("user", "usermail@mail.com");
    }

    /**
     * In this test the server is mocked to
     * simulate a communication exception on a second call.
     */
    @Test
    public <T extends SDId> void testMockServerExceptionOnSecondCall(@Mocked final UDDINaming uddi) throws Exception {

        new MockUp<T>() {
            @Mock
            void createUser(String name, String email) {
                if (x == 2)
                    throw new WebServiceException("fabricated");
                x++;
            }
        };

        new NonStrictExpectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                result = "http://localhost:8081";
            }
        };

        // Unit under test is exercised.
        IdClient client = new IdClient("http://localhost:8081", "sd-id");

        // first call to mocked server
        try {
            client.createUser("user", "usermail@mail.com");
        } catch (WebServiceException e) {
            // exception is not expected
            fail();
        }

        // second call to mocked server
        try {
            client.createUser("user2", "usermail2@mail.com");
            fail();
        } catch (WebServiceException e) {
            // exception is expected
            assertEquals("fabricated", e.getMessage());
        }
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