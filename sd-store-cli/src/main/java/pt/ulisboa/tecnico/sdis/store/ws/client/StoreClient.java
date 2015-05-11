package pt.ulisboa.tecnico.sdis.store.ws.client;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.client.uddi.UDDINaming;

public class StoreClient {
    /** WS service */
    SDStore_Service service = null;

    /** WS port (interface) */
    SDStore port = null;

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

    /**
     * constructor with provided web service URL
     * 
     * @throws JAXRException
     */
    public StoreClient(String uddiURL, String serviceName) throws StoreClientException, JAXRException {

        if (verbose)
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

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

        if (verbose)
            System.out.println("Creating stub ...");

        service = new SDStore_Service();
        port = service.getSDStoreImplPort();

        if (verbose)
            System.out.println("Setting endpoint address ...");

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
    }

    // SDStore

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        port.createDoc(docUserPair);
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        return port.listDocs(userId);
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        port.store(docUserPair, contents);
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        return port.load(docUserPair);
    }
}
