package pt.tecnico.bubbledocs.service.remote;

import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;

import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.CannotStoreDocumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.ServiceLookupException;
import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreRemoteServices extends SDRemoteServices implements SDStore {

    final private String uddiURL = "http://localhost:8081";
    final private String serviceName = "sd-store";

    /** WS service */
    protected SDStore_Service service = null;

    /** WS port (interface) */
    protected SDStore port = null;

    public StoreRemoteServices() {
        setVerbose(false);
        try {
            lookForService(uddiURL, serviceName);
            createStub();
        } catch (ServiceLookupException e) {
            throw new RemoteInvocationException();
        }
    }

    @Override
    protected void createStub() {
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

    public void storeDocument(String username, String docName, byte[] document) throws CannotStoreDocumentException,
            RemoteInvocationException {
        // TODO : the connection and invocation of the remote service
    }

    public byte[] loadDocument(String username, String docName) throws CannotLoadDocumentException, RemoteInvocationException {
        // TODO : the connection and invocation of the remote service
        return null;
    }

    @Override
    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        // TODO Auto-generated method stub
        return null;
    }

}