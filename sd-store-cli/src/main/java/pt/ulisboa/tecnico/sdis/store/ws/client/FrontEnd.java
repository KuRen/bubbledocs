package pt.ulisboa.tecnico.sdis.store.ws.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.client.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.store.ws.handler.FrontEndHandler;

public class FrontEnd {

    private int numberOfReplicas;
    private int writeThreshold;
    private int readThreshold;
    private List<SDStore> listOfReplicas;

    @SuppressWarnings("rawtypes")
    public FrontEnd(String uddiURL, String serviceName, int nReplicas, int WT, int RT) throws Exception {

        numberOfReplicas = nReplicas;
        writeThreshold = WT;
        readThreshold = RT;
        listOfReplicas = new ArrayList<SDStore>();

        UDDINaming uddi;

        try {
            for (int i = 0; i < numberOfReplicas; i++) {
                uddi = new UDDINaming(uddiURL);
                String url = uddi.lookup(serviceName + i);
                SDStore_Service service = new SDStore_Service();
                service.setHandlerResolver(new HandlerResolver() {
                    @Override
                    public List<Handler> getHandlerChain(PortInfo portInfo) {
                        List<Handler> handlerChain = new ArrayList<Handler>();
                        handlerChain.add(new FrontEndHandler());
                        return handlerChain;
                    }
                });
                SDStore port = service.getSDStoreImplPort();
                BindingProvider bindingProvider = (BindingProvider) port;
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
                listOfReplicas.add(port);
            }
        } catch (Exception e) {
            throw new Exception("One of the servers was not found!");
        }
    }

    // Quorum Consensus protocol not needed 
    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        for (SDStore replica : listOfReplicas) {
            replica.createDoc(docUserPair);
        }
    }

    // Quorum Consensus protocol not needed 
    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        List<String> listOfDocuments = null;
        for (SDStore replica : listOfReplicas) {
            listOfDocuments = replica.listDocs(userId);
        }
        return listOfDocuments;
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        int maxTag = -1;
        int acks = 0;
        int i;
        SDStore port;
        BindingProvider binding;
        Map<String, Object> context;
        boolean ack;
        for (i = 0; i < readThreshold; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            port.load(docUserPair);
            context = binding.getResponseContext();
            if ((int) context.get("tag") > maxTag)
                maxTag = (int) context.get("tag");
            binding.getRequestContext().remove("requestTag");
        }
        maxTag++;
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            port.store(docUserPair, contents);
            binding.getRequestContext().remove("newTag");
        }
        i = 0;
        while (acks < writeThreshold && i < numberOfReplicas) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getResponseContext();
            ack = (boolean) context.get("ack");
            if (ack)
                acks++;
            i++;
        }
        return;
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        int maxTag = -1;
        int acks = 0;
        int i;
        boolean ack;
        SDStore port;
        SDStore theChosenOne = null;
        BindingProvider binding;
        Map<String, Object> context;
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            port.load(docUserPair);
            binding.getRequestContext().remove("requestTag");
        }
        for (i = 0; i < readThreshold; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getResponseContext();
            if ((int) context.get("tag") > maxTag) {
                maxTag = (int) context.get("tag");
                theChosenOne = port;
            }
        }
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            try {
                port.store(docUserPair, theChosenOne.load(docUserPair));
            } catch (CapacityExceeded_Exception e) {
                e.printStackTrace();
            }
        }
        i = 0;
        while (acks < writeThreshold && i < numberOfReplicas) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getResponseContext();
            try {
                ack = (boolean) context.get("ack");
                if (ack)
                    acks++;
                i++;
            } catch (Exception e) {
                i++;
            }
        }
        return theChosenOne.load(docUserPair);
    }
}
