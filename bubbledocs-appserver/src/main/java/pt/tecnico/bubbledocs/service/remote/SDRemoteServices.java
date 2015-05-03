package pt.tecnico.bubbledocs.service.remote;

import javax.xml.registry.JAXRException;

import pt.tecnico.bubbledocs.exception.ServiceLookupException;
import pt.tecnico.bubbledocs.service.remote.uddi.UDDINaming;

public abstract class SDRemoteServices {

    /** Endpoint URL */
    protected String URL = null;

    /** output option **/
    protected boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public SDRemoteServices() {
    }

    protected abstract void createStub();

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

    public String getURL() {
        return URL;
    }

    public void setURL(String uRL) {
        URL = uRL;
    }

}
