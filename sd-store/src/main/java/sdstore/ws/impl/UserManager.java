package sdstore.ws.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;

public class UserManager {

    private Map<String, User> users = new HashMap<String, User>();

    public void addDocument(String userId, String documentId) throws DocAlreadyExists_Exception {
        if (users.get(userId) == null)
            users.put(userId, new User());
        users.get(userId).addDocument(documentId);
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        if (users.get(userId) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(userId);
            throw new UserDoesNotExist_Exception("The user with the userId " + userId + " does not exist", udne);
        }
        return users.get(userId).listDocs();
    }

    public void store(String userId, String documentId, byte[] contents) throws UserDoesNotExist_Exception,
            DocDoesNotExist_Exception, CapacityExceeded_Exception {
        if (users.get(userId) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(userId);
            throw new UserDoesNotExist_Exception("The user with the userId " + userId + " does not exist", udne);
        }
        users.get(userId).store(documentId, contents);
    }

    public byte[] load(String userId, String documentId) throws UserDoesNotExist_Exception, DocDoesNotExist_Exception {
        if (users.get(userId) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(userId);
            throw new UserDoesNotExist_Exception("The user with the userId " + userId + " does not exist", udne);
        }
        return users.get(userId).load(documentId);
    }
}
