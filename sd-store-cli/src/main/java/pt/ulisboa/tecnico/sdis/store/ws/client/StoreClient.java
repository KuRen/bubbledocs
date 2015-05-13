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

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        frontend.createDoc(docUserPair);
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        return frontend.listDocs(userId);
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws UserDoesNotExist_Exception, DocDoesNotExist_Exception {
        try {
            frontend.store(docUserPair, contents);
        } catch(UserDoesNotExist_Exception u) {
            throw u;
        } catch(DocDoesNotExist_Exception d) {
            throw d;
        } catch(Throwable t) {
            System.out.println("Unknown Error");
        }
    }

    public byte[] load(DocUserPair docUserPair) throws UserDoesNotExist_Exception, DocDoesNotExist_Exception, CapacityExceeded_Exception {
        try {
            return frontend.load(docUserPair);
        } catch(UserDoesNotExist_Exception u) {
            throw u;
        } catch(DocDoesNotExist_Exception d) {
            throw d;
        } catch(CapacityExceeded_Exception c) {
            throw c;
        } catch(Throwable t) {
            System.out.println("Unknown Error");
            return null;
        }
    }
}
