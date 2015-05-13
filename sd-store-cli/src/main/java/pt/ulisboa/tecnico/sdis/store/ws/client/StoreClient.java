package pt.ulisboa.tecnico.sdis.store.ws.client;

import java.util.List;
import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class StoreClient {
    
    FrontEnd frontend;

    public StoreClient(String uddiURL, String serviceName, int nReplicas, int WT, int RT) throws Exception {
        frontend = new FrontEnd(uddiURL, serviceName, nReplicas, WT, RT);
    }

    // SDStore

    public void createDoc(DocUserPair docUserPair, String ticket, String key) throws DocAlreadyExists_Exception {
        frontend.createDoc(docUserPair, ticket, key);
    }

    public List<String> listDocs(String userId, String ticket, String key) throws UserDoesNotExist_Exception {
        return frontend.listDocs(userId, ticket, key);
    }

    public void store(DocUserPair docUserPair, byte[] contents, String ticket, String key) throws UserDoesNotExist_Exception, DocDoesNotExist_Exception, CapacityExceeded_Exception {
        try {
            frontend.store(docUserPair, contents, ticket, key);
        } catch(UserDoesNotExist_Exception u) {
            throw (UserDoesNotExist_Exception) u;
        } catch(DocDoesNotExist_Exception d) {
            throw (DocDoesNotExist_Exception) d;
        } catch(CapacityExceeded_Exception c) {
            throw (CapacityExceeded_Exception) c;
        } catch(Throwable t) {
            System.out.println("Unknown Error");
        }
    }

    public byte[] load(DocUserPair docUserPair, String ticket, String key) throws UserDoesNotExist_Exception, DocDoesNotExist_Exception {
        try {
            return frontend.load(docUserPair, ticket, key);
        } catch(UserDoesNotExist_Exception u) {
            throw (UserDoesNotExist_Exception) u;
        } catch(DocDoesNotExist_Exception d) {
            throw (DocDoesNotExist_Exception) d;
        } catch(Throwable t) {
            System.out.println("Unknown Error");
            return null;
        }
    }
}
