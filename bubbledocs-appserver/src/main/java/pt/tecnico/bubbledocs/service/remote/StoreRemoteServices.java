package pt.tecnico.bubbledocs.service.remote;

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

public class StoreRemoteServices extends SDRemoteServices {

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

        if (username == null || docName == null)
            throw new CannotStoreDocumentException();

        DocUserPair pair = new DocUserPair();
        pair.setDocumentId(docName);
        pair.setUserId(username);
        try {
            port.store(pair, document);
        } catch (CapacityExceeded_Exception e) {
            throw new CannotStoreDocumentException();
        } catch (DocDoesNotExist_Exception e) {
            try {
                port.createDoc(pair);
            } catch (DocAlreadyExists_Exception e1) {
                throw new CannotStoreDocumentException();
            }
        } catch (UserDoesNotExist_Exception e) {
            throw new CannotStoreDocumentException();
        }
    }

    public byte[] loadDocument(String username, String docName) throws CannotLoadDocumentException, RemoteInvocationException {
        if (username == null || docName == null)
            throw new CannotStoreDocumentException();

        DocUserPair pair = new DocUserPair();
        pair.setDocumentId(docName);
        pair.setUserId(username);

        try {
            return port.load(pair);
        } catch (DocDoesNotExist_Exception e) {
            throw new CannotLoadDocumentException();
        } catch (UserDoesNotExist_Exception e) {
            throw new CannotLoadDocumentException();
        }

    }
}