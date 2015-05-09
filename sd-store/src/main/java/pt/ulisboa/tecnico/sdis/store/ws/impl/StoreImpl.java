package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", wsdlLocation = "SD-STORE.1_1.wsdl", name = "SdStore",
        portName = "SDStoreImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", serviceName = "SDStore")
@HandlerChain(file = "/backend-chain.xml")
public class StoreImpl implements SDStore {

    // Map<String, UserRepository> where String is userId
    private Map<String, UserRepository> repositories = new HashMap<String, UserRepository>();

    @Resource
    private WebServiceContext webServiceContext;

    public StoreImpl() throws Exception {
        populate4Test();
    }

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        if (docUserPair.getUserId() == null || docUserPair.getUserId().isEmpty() || docUserPair.getDocumentId() == null
                || docUserPair.getDocumentId().isEmpty())
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
        MessageContext context = webServiceContext.getMessageContext();
        if (context.get("newTag") != null) {
            if ((int) context.get("newTag") > repositories.get(docUserPair.getUserId()).getTag(docUserPair.getDocumentId())) {
                repositories.get(docUserPair.getUserId()).setTag(docUserPair.getDocumentId(), (int) context.get("newTag"));
                repositories.get(docUserPair.getUserId()).store(docUserPair.getDocumentId(), contents);
                context.put("ack", true);
                return;
            } else
                return;
        }
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        if (repositories.get(docUserPair.getUserId()) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(docUserPair.getUserId());
            throw new UserDoesNotExist_Exception("The user with the userId " + docUserPair.getUserId() + " does not exist", udne);
        }
        MessageContext context = webServiceContext.getMessageContext();
        if (context.get("requestTag") != null && (boolean) context.get("requestTag")) {
            context.put("tag", repositories.get(docUserPair.getUserId()).getTag(docUserPair.getDocumentId()));
            return null;
        }
        return repositories.get(docUserPair.getUserId()).load(docUserPair.getDocumentId());
    }

    public void populate4Test() throws Exception {
        repositories.clear();
        repositories.put("alice", new UserRepository());
        repositories.put("bruno", new UserRepository());
        repositories.put("carla", new UserRepository());
        repositories.put("duarte", new UserRepository());
        repositories.put("eduardo", new UserRepository());
        repositories.get("alice").addDocument("a1");
        repositories.get("alice").addDocument("a2");
        repositories.get("bruno").addDocument("b1");
        repositories.get("alice").store("a1", "AAAAAAAAAA".getBytes());
        repositories.get("alice").store("a2", "aaaaaaaaaa".getBytes());
        repositories.get("bruno").store("b1", "BBBBBBBBBBBBBBBBBBBB".getBytes());
    }
}
