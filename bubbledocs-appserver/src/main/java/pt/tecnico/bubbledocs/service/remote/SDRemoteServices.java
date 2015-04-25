package pt.tecnico.bubbledocs.service.remote;

import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.tecnico.bubbledocs.exception.ServiceLookupException;
import pt.tecnico.bubbledocs.service.remote.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;

public class SDRemoteServices {
    /** WS service */
    protected SDId_Service service = null;

    /** WS port (interface) */
    protected SDId port = null;

    /** Endpoint URL */
    private String URL = null;

    /** output option **/
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public SDRemoteServices() {
    }

    protected void createStub() {
        if (verbose)
            System.out.println("Creating stub ...");

        service = new SDId_Service();
        port = service.getSDIdImplPort();

        if (verbose)
            System.out.println("Setting endpoint address ...");

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
    }

    protected void lookForService(String uddiURL, String serviceName) throws ServiceLookupException {
        if (verbose)
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming;
        try {
            uddiNaming = new UDDINaming(uddiURL);

            if (verbose)
                System.out.printf("Looking for '%s'%n", serviceName);
            URL = uddiNaming.lookup(serviceName);

            if (URL == null && verbose) {
                System.out.println("Not found!");
                return;
            } else {
                if (verbose)
                    System.out.printf("Found %s%n", URL);
            }

        } catch (JAXRException e) {
            throw new ServiceLookupException();
        }
    }

    public SDId_Service getService() {
        return service;
    }

    public void setService(SDId_Service service) {
        this.service = service;
    }

    public SDId getPort() {
        return port;
    }

    public void setPort(SDId port) {
        this.port = port;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

}
