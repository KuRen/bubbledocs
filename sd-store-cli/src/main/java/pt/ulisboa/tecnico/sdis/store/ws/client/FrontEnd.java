package pt.ulisboa.tecnico.sdis.store.ws.client;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Response;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;

import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.LoadResponse;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.StoreResponse;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.handler.FrontEndHandler;

public class FrontEnd {

    private int numberOfReplicas;
    private int writeThreshold;
    private int readThreshold;
    private String nonce;
    private List<SDStore> listOfReplicas;

    @SuppressWarnings("rawtypes")
    public FrontEnd(String uddiURL, String serviceName, int nReplicas, int WT, int RT) throws Exception {

        numberOfReplicas = nReplicas;
        writeThreshold = WT;
        readThreshold = RT;
        listOfReplicas = new ArrayList<SDStore>();

        UDDINaming uddi;

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
    }

    private BindingProvider putToHandler(String ticket, SDStore replica, String key, String user) {
        BindingProvider bindingProvider;
        Map<String, Object> requestContext;
        bindingProvider = (BindingProvider) replica;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(FrontEndHandler.REQUEST_TICKET, ticket);
        String auth = null;
        //try {
        auth = cipherXML(makeAuth(user), key);
        /*} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace(); //FIXME
        }*/
        requestContext.put(FrontEndHandler.REQUEST_AUTH, auth);

        nonce = Integer.toString(new SecureRandom().nextInt());
        requestContext.put(FrontEndHandler.REQUEST_NONCE, nonce);

        return bindingProvider;
    }

    private String makeAuth(String user) {
        DateTime now = new DateTime();

        Document auth = new Document();
        XMLOutputter xmlOutputter = new XMLOutputter();

        Element rootElement = new Element("CipheredAuth");
        Element userElement = new Element("User");
        Element timeElement = new Element("Time");

        userElement.setText(user);
        timeElement.setText(now.toString());

        rootElement.addContent(userElement);
        rootElement.addContent(timeElement);

        auth.setRootElement(rootElement);

        return xmlOutputter.outputString(auth);
    }

    private String cipherXML(String xml, String key) {
        byte[] bytes = xml.getBytes();
        // generate a secret key
        Key CSkey = new SecretKeySpec(parseBase64Binary(key), "AES");
        System.out.println("Made key! size: " + bytes.length);
        // get a AES cipher object
        Cipher cipher;

        byte[] cipherBytes = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            System.out.println("Got Cypher!");
            // encrypt using the key and the plaintext
            cipher.init(Cipher.ENCRYPT_MODE, CSkey, new IvParameterSpec(new byte[16]));
            cipherBytes = cipher.doFinal(bytes);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException
                | IllegalBlockSizeException | BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return printBase64Binary(cipherBytes);
    }

    // Quorum Consensus protocol not needed 
    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        for (SDStore replica : listOfReplicas) {
            replica.createDoc(docUserPair);
        }
    }

    //Using handler
    public void createDoc(DocUserPair docUserPair, String ticket, String key) throws DocAlreadyExists_Exception {
        for (SDStore replica : listOfReplicas) {
            putToHandler(ticket, replica, key, docUserPair.getUserId());
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

    //Using handler
    public List<String> listDocs(String userId, String ticket, String key) throws UserDoesNotExist_Exception {
        BindingProvider bindingProvider = null;

        List<String> listOfDocuments = null;
        for (SDStore replica : listOfReplicas) {
            bindingProvider = putToHandler(ticket, replica, key, userId);

            listOfDocuments = replica.listDocs(userId);
        }

        Map<String, Object> responseContext = bindingProvider.getResponseContext();

        String finalValue = (String) responseContext.get(FrontEndHandler.RESPONSE_HEADER);

        return listOfDocuments;
    }

    public void store(DocUserPair docUserPair, byte[] contents) throws Throwable {
        int maxTag = -1;
        int acks = 0;
        int i;
        SDStore port;
        BindingProvider binding;
        Map<String, Object> context;
        boolean ack;
        List<Response<LoadResponse>> listOfLoadResponses = new ArrayList<Response<LoadResponse>>();
        List<Response<StoreResponse>> listOfStoreResponses = new ArrayList<Response<StoreResponse>>();
        for (i = 0; i < readThreshold; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            Response<LoadResponse> response = port.loadAsync(docUserPair);
            listOfLoadResponses.add(response);
            binding.getRequestContext().remove("requestTag");
        }
        i = 0;
        while (i < readThreshold) {
            for (Response<LoadResponse> resp : new ArrayList<>(listOfLoadResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfLoadResponses.remove(resp);
                        context = resp.getContext();
                        if ((int) context.get("tag") > maxTag)
                            maxTag = (int) context.get("tag");
                        i++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        maxTag++;
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            Response<StoreResponse> response = port.storeAsync(docUserPair, contents);
            listOfStoreResponses.add(response);
            binding.getRequestContext().remove("newTag");
        }
        while (acks < writeThreshold) {
            for (Response<StoreResponse> resp : new ArrayList<>(listOfStoreResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfStoreResponses.remove(resp);
                        context = resp.getContext();
                        ack = (boolean) context.get("ack");
                        if (ack)
                            acks++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        return;
    }

    //Using handler
    public void store(DocUserPair docUserPair, byte[] contents, String ticket, String key) throws Throwable {
        int maxTag = -1;
        int acks = 0;
        int i;
        SDStore port;
        BindingProvider binding;
        Map<String, Object> context;
        boolean ack;
        List<Response<LoadResponse>> listOfLoadResponses = new ArrayList<Response<LoadResponse>>();
        List<Response<StoreResponse>> listOfStoreResponses = new ArrayList<Response<StoreResponse>>();
        for (i = 0; i < readThreshold; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            Response<LoadResponse> response = port.loadAsync(docUserPair);
            listOfLoadResponses.add(response);
            binding.getRequestContext().remove("requestTag");
        }
        i = 0;
        while (i < readThreshold) {
            for (Response<LoadResponse> resp : new ArrayList<>(listOfLoadResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfLoadResponses.remove(resp);
                        context = resp.getContext();
                        if ((int) context.get("tag") > maxTag)
                            maxTag = (int) context.get("tag");
                        i++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        maxTag++;
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            putToHandler(ticket, port, key, docUserPair.getUserId());
            Response<StoreResponse> response = port.storeAsync(docUserPair, contents);
            listOfStoreResponses.add(response);
            binding.getRequestContext().remove("newTag");
        }
        while (acks < writeThreshold) {
            for (Response<StoreResponse> resp : new ArrayList<>(listOfStoreResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfStoreResponses.remove(resp);
                        context = resp.getContext();
                        ack = (boolean) context.get("ack");
                        if (ack)
                            acks++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        return;
    }

    public byte[] load(DocUserPair docUserPair) throws Throwable {
        int maxTag = -1;
        int acks = 0;
        int i;
        boolean ack;
        SDStore port;
        Response<LoadResponse> theChosenOne = null;
        BindingProvider binding;
        Map<String, Object> context;
        List<Response<LoadResponse>> listOfLoadResponses = new ArrayList<Response<LoadResponse>>();
        List<Response<StoreResponse>> listOfStoreResponses = new ArrayList<Response<StoreResponse>>();
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            Response<LoadResponse> response = port.loadAsync(docUserPair);
            listOfLoadResponses.add(response);
            binding.getRequestContext().remove("requestTag");
        }
        i = 0;
        while (i < readThreshold) {
            for (Response<LoadResponse> resp : new ArrayList<>(listOfLoadResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfLoadResponses.remove(resp);
                        context = resp.getContext();
                        if ((int) context.get("tag") > maxTag) {
                            maxTag = (int) context.get("tag");
                            theChosenOne = resp;
                        }
                        i++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            try {
                Response<StoreResponse> response = port.storeAsync(docUserPair, theChosenOne.get().getContents());
                listOfStoreResponses.add(response);
            } catch (ExecutionException | InterruptedException e) {
                throw e.getCause();
            }
        }
        while (acks < writeThreshold) {
            for (Response<StoreResponse> resp : new ArrayList<>(listOfStoreResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfStoreResponses.remove(resp);
                        context = resp.getContext();
                        ack = (boolean) context.get("ack");
                        if (ack)
                            acks++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        try {
            return theChosenOne.get().getContents();
        } catch (ExecutionException | InterruptedException e) {
            throw e.getCause();
        }
    }

    //Using handler
    public byte[] load(DocUserPair docUserPair, String ticket, String key) throws Throwable {
        int maxTag = -1;
        int acks = 0;
        int i;
        boolean ack;
        SDStore port;
        Response<LoadResponse> theChosenOne = null;
        BindingProvider binding = null;
        Map<String, Object> context;
        List<Response<LoadResponse>> listOfLoadResponses = new ArrayList<Response<LoadResponse>>();
        List<Response<StoreResponse>> listOfStoreResponses = new ArrayList<Response<StoreResponse>>();
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("requestTag", true);
            putToHandler(ticket, port, key, docUserPair.getUserId());
            Response<LoadResponse> response = port.loadAsync(docUserPair);
            listOfLoadResponses.add(response);
            binding.getRequestContext().remove("requestTag");
        }
        i = 0;
        while (i < readThreshold) {
            for (Response<LoadResponse> resp : new ArrayList<>(listOfLoadResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfLoadResponses.remove(resp);
                        context = resp.getContext();
                        if ((int) context.get("tag") > maxTag) {
                            maxTag = (int) context.get("tag");
                            theChosenOne = resp;
                        }
                        i++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }
        for (i = 0; i < numberOfReplicas; i++) {
            port = listOfReplicas.get(i);
            binding = (BindingProvider) port;
            context = binding.getRequestContext();
            context.put("newTag", maxTag);
            /*MessageDigest cript = null;
            try {
                cript = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            cript.reset();
            try {
                cript.update(theChosenOne.get().getContents());
            } catch (ExecutionException | InterruptedException e1) {
                e1.printStackTrace();
            }*/
            try {
                Response<StoreResponse> response = port.storeAsync(docUserPair, theChosenOne.get().getContents());
                listOfStoreResponses.add(response);
            } catch (ExecutionException | InterruptedException e) {
                throw e.getCause();
            }
        }
        while (acks < writeThreshold) {
            for (Response<StoreResponse> resp : new ArrayList<>(listOfStoreResponses)) {
                try {
                    if (resp.get() != null) {
                        listOfStoreResponses.remove(resp);
                        context = resp.getContext();
                        ack = (boolean) context.get("ack");
                        if (ack)
                            acks++;
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw e.getCause();
                }
            }
        }

        // Map<String, Object> responseContext = binding.getResponseContext();

        // String finalValue = (String) responseContext.get(FrontEndHandler.RESPONSE_HEADER);

        try {
            return theChosenOne.get().getContents();
        } catch (ExecutionException | InterruptedException e) {
            throw e.getCause();
        }
    }
}
