package pt.ulisboa.tecnico.sdis.store.ws.impl;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.store.ws.impl.uddi.UDDINaming;

public class StoreServer {

    private Endpoint endpoint = null;
    private UDDINaming uddiNaming = null;
    private String name;
    private String url;
    private String uddiURL;
    private boolean verbose = false;

    public StoreServer(String uddiURL, String name, String url) {
        this.name = name;
        this.url = url;
        this.uddiURL = uddiURL;
    }

    public void run() throws Exception {
        endpoint = Endpoint.create(new StoreImpl());

        // Publish endpoint
        if (verbose)
            System.out.printf("Starting %s%n", url);
        endpoint.publish(url);

        // Publish to UDDI
        if (verbose)
            System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
        uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);
    }

    public void stop() {
        try {
            if (endpoint != null) {
                // Stop endpoint
                endpoint.stop();
                if (verbose)
                    System.out.printf("Stopped %s%n", url);
            }
        } catch (Exception e) {
            System.out.printf("Caught exception when stopping: %s%n", e);
        }
        try {
            if (uddiNaming != null) {
                // Delete from UDDI
                uddiNaming.unbind(name);
                if (verbose)
                    System.out.printf("Deleted '%s' from UDDI%n", name);
            }
        } catch (Exception e) {
            System.out.printf("Caught exception when deleting: %s%n", e);
        }
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}