package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded;
import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;

public class User {

    private static final int MAX_CAPACITY = 1024 * 10;
    private Map<String, Document> docs = new HashMap<String, Document>();
    private int capacity = 0;

    public void addDocument(String documentId) throws DocAlreadyExists_Exception {
        if (docs.get(documentId) != null) {
            DocAlreadyExists dae = new DocAlreadyExists();
            dae.setDocId(documentId);
            throw new DocAlreadyExists_Exception("This user already has a document called " + documentId, dae);
        }
        docs.put(documentId, new Document());
    }

    public List<String> listDocs() {
        List<String> documents = new ArrayList<String>(docs.keySet());
        Collections.sort(documents);
        return documents;
    }

    public void store(String documentId, byte[] contents) throws DocDoesNotExist_Exception, CapacityExceeded_Exception {
        if (docs.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        if (capacity + contents.length - docs.get(documentId).getSize() > MAX_CAPACITY) {
            CapacityExceeded ce = new CapacityExceeded();
            ce.setAllowedCapacity(MAX_CAPACITY);
            ce.setCurrentSize(capacity);
            throw new CapacityExceeded_Exception("This user exceeded the maximum capacity of his repository", ce);
        }
        capacity -= docs.get(documentId).getSize();
        docs.get(documentId).setContent(contents);
        capacity += docs.get(documentId).getSize();
    }

    public byte[] load(String documentId) throws DocDoesNotExist_Exception {
        if (docs.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        return docs.get(documentId).getContent();
    }
}
