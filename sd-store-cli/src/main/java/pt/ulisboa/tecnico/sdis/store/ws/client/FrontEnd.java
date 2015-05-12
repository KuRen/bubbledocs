package pt.ulisboa.tecnico.sdis.store.ws.client;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;

import pt.ulisboa.tecnico.sdis.store.ws.CapacityExceeded_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore_Service;
import pt.ulisboa.tecnico.sdis.store.ws.UserDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.client.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.store.ws.handler.FrontEndHandler;
import pt.ulisboa.tecnico.sdis.store.ws.handler.RelayClientHandler;

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

    private BindingProvider putToHandler(String ticket, SDStore replica, String key, String user) {
        BindingProvider bindingProvider;
        Map<String, Object> requestContext;
        bindingProvider = (BindingProvider) replica;
        requestContext = bindingProvider.getRequestContext();
        requestContext.put(RelayClientHandler.REQUEST_TICKET, ticket);
        String auth = null;
        try {
            auth = cipherXML(makeAuth(user), key);
        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException
                | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace(); //FIXME
        }
        requestContext.put(RelayClientHandler.REQUEST_AUTH, auth);

        nonce = Integer.toString(new SecureRandom().nextInt());
        requestContext.put(RelayClientHandler.REQUEST_NONCE, nonce);

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

    private String cipherXML(String xml, String key) throws NoSuchAlgorithmException, InvalidKeyException,
            InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        byte[] bytes = xml.getBytes();
        // generate a secret key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey CSkey = factory.generateSecret(new DESKeySpec(key.getBytes()));

        // get a DES cipher object
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        // encrypt using the key and the plaintext
        cipher.init(Cipher.ENCRYPT_MODE, CSkey);
        byte[] cipherBytes = cipher.doFinal(bytes);

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

        String finalValue = (String) responseContext.get(RelayClientHandler.RESPONSE_HEADER);

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

    //Using handler
    public void store(DocUserPair docUserPair, byte[] contents, String ticket, String key) throws CapacityExceeded_Exception,
            DocDoesNotExist_Exception, UserDoesNotExist_Exception {
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
            putToHandler(ticket, port, key, docUserPair.getUserId());
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

    //Using handler
    public byte[] load(DocUserPair docUserPair, String ticket, String key) throws DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        int maxTag = -1;
        int acks = 0;
        int i;
        boolean ack;
        SDStore port;
        SDStore theChosenOne = null;
        BindingProvider binding = null;
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
            putToHandler(ticket, port, key, docUserPair.getUserId());

            MessageDigest cript = null;
            try {
                cript = MessageDigest.getInstance("SHA-1");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            cript.reset();
            cript.update(theChosenOne.load(docUserPair));
            byte[] hash = cript.digest();

            context.put(RelayClientHandler.REQUEST_TICKET, printBase64Binary(hash));

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

        Map<String, Object> responseContext = binding.getResponseContext();

        String finalValue = (String) responseContext.get(RelayClientHandler.RESPONSE_HEADER);

        return theChosenOne.load(docUserPair);
    }
}
