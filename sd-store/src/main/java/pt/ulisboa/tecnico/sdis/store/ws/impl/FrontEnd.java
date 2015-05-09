package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.jws.WebService;
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
import pt.ulisboa.tecnico.sdis.store.ws.handler.FrontEndHandler;
import pt.ulisboa.tecnico.sdis.store.ws.impl.uddi.UDDINaming;

@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", wsdlLocation = "SD-STORE.1_1.wsdl", name = "SdStore",
        portName = "SDStoreImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", serviceName = "SDStore")
@HandlerChain(file = "/client-chain.xml")
public class FrontEnd implements SDStore {

    private List<SDStore> listOfReplicas;
    private int writeThreshold;
    private int readThreshold;
    private int numberOfReplicas;

    @SuppressWarnings("rawtypes")
    public FrontEnd(String uddiURL, String serviceName, int nReplicas, int WT, int RT) throws Exception {
        numberOfReplicas = nReplicas;
        writeThreshold = WT;
        readThreshold = RT;
        UDDINaming uddi;
        listOfReplicas = new ArrayList<SDStore>();
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

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception { // no need to quorum
        for (SDStore replica : listOfReplicas) {
            replica.createDoc(docUserPair);
        }
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception { // no need to quorum
        List<String> listOfDocuments = null;
        for (SDStore replica : listOfReplicas) {
            listOfDocuments = replica.listDocs(userId);
        }
        return listOfDocuments;
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        int maxTag = -1;
        int newTag;
        int currentReadQuorum = 0;
        int acks = 0;
        List<Integer> listOfTags = new ArrayList<Integer>();
        List<SDStore> listOfReadReplicas = new ArrayList<SDStore>();
        List<SDStore> listOfWriteReplicas = new ArrayList<SDStore>();
        for (int i = 0; i < numberOfReplicas; i++) {
            try {
                while (currentReadQuorum < readThreshold) {
                    SDStore port = listOfReplicas.get(i);
                    BindingProvider binding = (BindingProvider) port;
                    Map<String, Object> context = binding.getRequestContext();
                    context.put("requestTag", true);
                    port.load(docUserPair);
                    listOfReadReplicas.add(port);
                    context = binding.getResponseContext();
                    listOfTags.add((int) context.get("tag"));
                    currentReadQuorum++;
                    binding.getRequestContext().remove("requestTag");
                }
                break;
            } catch (Exception e) {
                continue;
            }
        }
        for (int i = 0; i < listOfTags.size(); i++) {
            if (listOfTags.get(i) > maxTag)
                maxTag = listOfTags.get(i);
        }
        newTag = maxTag + 1;
        for (int i = 0; i < numberOfReplicas; i++) {
            try {
                SDStore port = listOfReplicas.get(i);
                BindingProvider binding = (BindingProvider) port;
                Map<String, Object> context = binding.getRequestContext();
                context.put("newTag", newTag);
                port.store(docUserPair, contents);
                listOfWriteReplicas.add(port);
                binding.getRequestContext().remove("newTag");
            } catch (Exception e) {
                continue;
            }
        }
        for (int i = 0; i < listOfWriteReplicas.size(); i++) {
            while (acks < writeThreshold) {
                SDStore port = listOfWriteReplicas.get(i);
                BindingProvider binding = (BindingProvider) port;
                Map<String, Object> context = binding.getResponseContext();
                port.store(docUserPair, contents);
                try {
                    boolean ack = (boolean) context.get("ack");
                    if (ack)
                        acks++;
                } catch (Exception e) {
                    break;
                }
            }
        }
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        int maxTag = -1;
        int currentQuorum = 0;
        SDStore theChosenOne = null;
        List<Integer> listOfTags = new ArrayList<Integer>();
        List<SDStore> listOfReadReplicas = new ArrayList<SDStore>();
        for (int i = 0; i < numberOfReplicas; i++) {
            try {
                while (currentQuorum < readThreshold) {
                    SDStore port = listOfReplicas.get(i);
                    BindingProvider binding = (BindingProvider) port;
                    Map<String, Object> context = binding.getRequestContext();
                    context.put("requestTag", true);
                    port.load(docUserPair);
                    listOfReadReplicas.add(port);
                    context = binding.getResponseContext();
                    listOfTags.add((int) context.get("tag"));
                    currentQuorum++;
                }
                break;
            } catch (Exception e) {
                continue;
            }
        }
        for (int i = 0; i < listOfTags.size(); i++) {
            if (listOfTags.get(i) > maxTag)
                maxTag = listOfTags.get(i);
        }
        for (int i = 0; i < listOfReadReplicas.size(); i++) {
            SDStore port = listOfReadReplicas.get(i);
            BindingProvider binding = (BindingProvider) port;
            Map<String, Object> context = binding.getRequestContext();
            context.put("requestTag", true);
            context = binding.getResponseContext();
            if ((int) context.get("tag") == maxTag) {
                theChosenOne = port;
                binding.getRequestContext().remove("requestTag");
                break;
            }
            binding.getRequestContext().remove("requestTag");
        }
        return theChosenOne.load(docUserPair);
    }
}
