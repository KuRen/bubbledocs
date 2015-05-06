package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.List;

import javax.jws.HandlerChain;
import javax.jws.WebService;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", wsdlLocation = "SD-STORE.1_1.wsdl", name = "SdStore",
        portName = "SDStoreImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", serviceName = "SDStore")
@HandlerChain(file = "/handler-chain.xml")
public class StoreImpl implements SDStore {

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        UserManager.getInstance().addDocument(docUserPair.getUserId(), docUserPair.getDocumentId());
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        return UserManager.getInstance().listDocs(userId);
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        UserManager.getInstance().store(docUserPair.getUserId(), docUserPair.getDocumentId(), contents);
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        return UserManager.getInstance().load(docUserPair.getUserId(), docUserPair.getDocumentId());
    }
}
