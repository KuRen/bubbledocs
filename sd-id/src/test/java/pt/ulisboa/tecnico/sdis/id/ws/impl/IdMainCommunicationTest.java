package pt.ulisboa.tecnico.sdis.id.ws.impl;

import java.lang.reflect.Field;

import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.sdis.id.ws.impl.uddi.UDDINaming;

public class IdMainCommunicationTest {

    private static final String NAME = "name";
    private static final String URL = "url.com";
    private static final String UDDIUrl = "uddi.url";

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = UserManager.class.getDeclaredField("instance");
        field.setAccessible(true);
        field.set(null, null);
    }

    @Test
    public void emptyUDDIURL(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = new JAXRException();
                endpoint.stop();
            }
        };

        String[] args = { "", NAME, URL };
        IdMain.main(args);

    }

    @Test
    public void nullUDDIURL(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = new JAXRException();
                endpoint.stop();
            }
        };

        String[] args = { null, NAME, URL };
        IdMain.main(args);

    }

    @Test
    public void emptyName(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = uddi;
                uddi.rebind(anyString, anyString);
                result = new JAXRException();
                endpoint.stop();
                uddi.unbind(anyString);
            }
        };

        String[] args = { UDDIUrl, "", URL };
        IdMain.main(args);

    }

    @Test
    public void nullName(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = uddi;
                uddi.rebind(anyString, anyString);
                result = new JAXRException();
                endpoint.stop();
                uddi.unbind(anyString);
            }
        };

        String[] args = { UDDIUrl, null, URL };
        IdMain.main(args);

    }

    @Test
    public void emptyUDDIUrl(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = uddi;
                uddi.rebind(anyString, anyString);
                result = new JAXRException();
                endpoint.stop();
                uddi.unbind(anyString);
            }
        };

        String[] args = { UDDIUrl, NAME, "" };
        IdMain.main(args);

    }

    @Test
    public void nullUDDIUrl(@Mocked final UDDINaming uddi, @Mocked final Endpoint endpoint) throws JAXRException {
        new Expectations() {
            {
                Endpoint.create(withInstanceOf(IdImpl.class));
                result = endpoint;
                endpoint.publish(anyString);
                new UDDINaming(anyString);
                result = uddi;
                uddi.rebind(anyString, anyString);
                result = new JAXRException();
                endpoint.stop();
                uddi.unbind(anyString);
            }
        };

        String[] args = { UDDIUrl, NAME, null };
        IdMain.main(args);

    }

}
