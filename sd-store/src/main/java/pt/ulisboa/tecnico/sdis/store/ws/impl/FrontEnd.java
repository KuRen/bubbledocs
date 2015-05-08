package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.registry.JAXRException;
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
    
    private List<SDStore> list;
    private int writeThreshold;
    private int readThreshold;
    private int nServers;
    
    public FrontEnd(String uddiURL, String serviceName, int servern, int write, int read) throws Exception {
        nServers = servern;
        writeThreshold = write;
        readThreshold = read;
        UDDINaming uddi;
        list = new ArrayList<SDStore>();
        try {
            for(int i = 0; i < nServers; i++) {
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
                list.add(port);
            }
        } catch(Exception e) {
            throw new Exception("One of the servers was not found!");
        }
    }

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception { // no need to quorum
        for(SDStore port : list) {
            port.createDoc(docUserPair);
        }
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception { // no need to quorum
        List<String> ret = null;
        for(SDStore port : list) {
            ret = port.listDocs(userId);
        }
        return ret;
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        int tag = -1;
        int acks = 0;
        for(int i = 0; i < readThreshold; i++) {
            SDStore port = list.get(i);
            BindingProvider binding = (BindingProvider) port;
            Map<String,Object> context = binding.getRequestContext();
            context.put("requestTag", true);
            port.load(docUserPair);
            context = binding.getResponseContext();
            int newtag = (int) context.get("tag");
            if(newtag > tag)
                tag = newtag;
            binding.getRequestContext().remove("requestTag");
        }
        tag++;
        for(SDStore port : list) {
            BindingProvider binding = (BindingProvider) port;
            Map<String,Object> context = binding.getRequestContext();
            context.put("newTag", tag);
            port.store(docUserPair, contents);
            context = binding.getResponseContext();
            boolean ack = (boolean) context.get("Ack");
            if(ack) acks++;
            if(acks > writeThreshold)
                break;
            binding.getRequestContext().remove("newTag");
        }
        //if(acks < writeThreshold) throw new JAXRException(); //TODO
        return;
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        int tag = -1;
        SDStore theChosenOne = null;
        for(int i = 0; i < readThreshold; i++) {
            SDStore port = list.get(i);
            BindingProvider binding = (BindingProvider) port;
            Map<String,Object> context = binding.getRequestContext();
            context.put("requestTag", true);
            port.load(docUserPair);
            context = binding.getResponseContext();
            int newtag = (int) context.get("tag");
            if(newtag > tag)
                theChosenOne = port;
            binding.getRequestContext().remove("requestTag");
        }
        return theChosenOne.load(docUserPair);
    }
}
