package pt.ulisboa.tecnico.sdis.id.client;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;

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
        client = new IdClient();
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

        client.lookForService("some.url", "Some-service-name");
    }

    @Test
    public void success(@Mocked final UDDINaming uddi, @Mocked final SDId_Service service, @Mocked final SDId port,
            @Mocked final BindingProvider bindingProvider) throws JAXRException, serviceFindException {
        new Expectations() {
            {
                new UDDINaming(anyString);
                uddi.lookup(anyString);
                new SDId_Service();
                result = service;
                service.getSDIdImplPort();
                result = port;
                bindingProvider.getRequestContext();
            }
        };

        client.lookForService("some.url", "Some-service-name");
        client.createStub();
    }

}