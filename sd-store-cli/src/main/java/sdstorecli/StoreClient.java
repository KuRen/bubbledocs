package sdstorecli;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreClient implements SDStore {
    /** WS service */
    SDStore_Service service = null;

    /** WS port (interface) */
    SDStore port = null;

    /** WS endpoint address */
    // default value is defined by WSDL
    private String wsURL = null;

    /** output option **/
    private boolean verbose = false;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /** constructor with provided web service URL */
    public StoreClient(String wsURL) throws StoreClientException {
        this.wsURL = wsURL;
        createStub();
    }

    /** default constructor uses default endpoint address */
    public StoreClient() throws StoreClientException {
        createStub();
    }

    /** Stub creation and configuration */
    protected void createStub() {
        if (verbose)
            System.out.println("Creating stub ...");
        service = new SDStore_Service();
        port = service.getSDStoreImplPort();

        if (wsURL != null) {
            if (verbose)
                System.out.println("Setting endpoint address ...");
            BindingProvider bindingProvider = (BindingProvider) port;
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, wsURL);
        }
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
