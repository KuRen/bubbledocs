package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;

/*
 *  Commented code present in this file is due to the fact that capacity isn't considered in this part of the project.
 * */

public class UserRepository {

    //private static final int MAX_CAPACITY = 1024 * 10;
    //private int capacity = 0;

    // Map<String, Document> where String is the documentId
    private Map<String, Document> documents = new HashMap<String, Document>();

    public void addDocument(String documentId) throws DocAlreadyExists_Exception {
        if (documents.get(documentId) != null) {
            DocAlreadyExists dae = new DocAlreadyExists();
            dae.setDocId(documentId);
            throw new DocAlreadyExists_Exception("This user already has a document called " + documentId, dae);
        }
        documents.put(documentId, new Document());
    }

    public List<String> listDocs() {
        List<String> documents = new ArrayList<String>(this.documents.keySet());
        Collections.sort(documents);
        return documents;
    }

    public void store(String documentId, byte[] contents) throws DocDoesNotExist_Exception, CapacityExceeded_Exception {
        if (documents.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        /* 
        if (capacity + contents.length - documents.get(documentId).getSize() > MAX_CAPACITY) {
            CapacityExceeded ce = new CapacityExceeded();
            ce.setAllowedCapacity(MAX_CAPACITY);
            ce.setCurrentSize(capacity);
            throw new CapacityExceeded_Exception("This user exceeded the maximum capacity of his repository", ce);
        }
        capacity -= documents.get(documentId).getSize();
        */
        documents.get(documentId).setContent(contents);
        //capacity += documents.get(documentId).getSize();
    }

    public byte[] load(String documentId) throws DocDoesNotExist_Exception {
        if (documents.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        return documents.get(documentId).getContent();
    }

    public int getTag(String documentId) throws DocDoesNotExist_Exception {
        if (documents.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        return documents.get(documentId).getTag();
    }

    public void setTag(String documentId, int newTag) throws DocDoesNotExist_Exception {
        if (documents.get(documentId) == null) {
            DocDoesNotExist ddne = new DocDoesNotExist();
            ddne.setDocId(documentId);
            throw new DocDoesNotExist_Exception("This user does not have a document called " + documentId, ddne);
        }
        documents.get(documentId).setTag(newTag);
    }
}
