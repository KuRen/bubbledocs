package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.impl.uddi.UDDINaming;

@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.store.ws.SDStore", wsdlLocation = "SD-STORE.1_1.wsdl", name = "SdStore",
        portName = "SDStoreImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:store:ws", serviceName = "SDStore")
@HandlerChain(file = "/handler-chain.xml")
public class FrontEnd implements SDStore {
    
    private List<SDStore> list;
    
    public FrontEnd(String uddiURL, String serviceName) throws Exception {
        UDDINaming uddi;
        int i = 0;
        list = new ArrayList<SDStore>();
        try {
            while(true) {
                uddi = new UDDINaming(uddiURL);
                String url = uddi.lookup(serviceName + i);
                SDStore port = new SDStore_Service().getSDStoreImplPort();
                BindingProvider bindingProvider = (BindingProvider) port;
                Map<String, Object> requestContext = bindingProvider.getRequestContext();
                requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
                list.add(port);
                i++;
            }
        } catch(Exception e) {
            if(i == 0) throw new Exception("No servers found.");
        }
    }

    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        for(SDStore port : list) {
            port.createDoc(docUserPair);
        }
    }

    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        for(SDStore port : list) {
            port.listDocs(userId);
        }
        return null;
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        for(SDStore port : list) {
            port.store(docUserPair, contents);
        }
    }

    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        for(SDStore port : list) {
            port.load(docUserPair);
        }
        return null;
    }
}
