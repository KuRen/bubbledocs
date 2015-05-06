package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", wsdlLocation = "SD-STORE.1_1.wsdl", name = "SdStore",
        portName = "SDStoreImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", serviceName = "SDStore")
@HandlerChain(file = "/handler-chain.xml")
public class StoreImpl implements SDStore {
    
    private Map<String, UserRepository> repositories = new HashMap<String, UserRepository>();
    
    public StoreImpl() {
        populate4Test();
    }

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        if(docUserPair.getUserId() == null || docUserPair.getUserId().isEmpty() || docUserPair.getDocumentId() == null || docUserPair.getDocumentId().isEmpty())
            return;
        if (repositories.get(docUserPair.getUserId()) == null)
            repositories.put(docUserPair.getUserId(), new UserRepository());
        repositories.get(docUserPair.getUserId()).addDocument(docUserPair.getDocumentId());
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        if (repositories.get(userId) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(userId);
            throw new UserDoesNotExist_Exception("The user with the userId " + userId + " does not exist", udne);
        }
        return repositories.get(userId).listDocs();
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        if (repositories.get(docUserPair.getUserId()) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(docUserPair.getUserId());
            throw new UserDoesNotExist_Exception("The user with the userId " + docUserPair.getUserId() + " does not exist", udne);
        }
        repositories.get(docUserPair.getUserId()).store(docUserPair.getDocumentId(), contents);
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        if (repositories.get(docUserPair.getUserId()) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(docUserPair.getUserId());
            throw new UserDoesNotExist_Exception("The user with the userId " + docUserPair.getUserId() + " does not exist", udne);
        }
        return repositories.get(docUserPair.getUserId()).load(docUserPair.getDocumentId());
    }
    
    public void populate4Test() {
        repositories.clear();
        repositories.put("alice", new UserRepository());
        repositories.put("bruno", new UserRepository());
        repositories.put("carla", new UserRepository());
        repositories.put("duarte", new UserRepository());
        repositories.put("eduardo", new UserRepository());
    }
}
