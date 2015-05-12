package pt.ulisboa.tecnico.sdis.store.ws.impl;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.jws.HandlerChain;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.joda.time.DateTime;

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

    private String clientServerKey = null;

    @Resource
    private WebServiceContext webServiceContext;

    public StoreImpl() throws Exception {
        populate4Test();
    }

    @Override
    public void createDoc(DocUserPair docUserPair) throws DocAlreadyExists_Exception {
        if (docUserPair.getUserId() == null || docUserPair.getUserId().isEmpty() || docUserPair.getDocumentId() == null
                || docUserPair.getDocumentId().isEmpty())
            return;
        if (repositories.get(docUserPair.getUserId()) == null)
            repositories.put(docUserPair.getUserId(), new UserRepository());
        repositories.get(docUserPair.getUserId()).addDocument(docUserPair.getDocumentId());
    }

    @Override
    public List<String> listDocs(String userId) throws UserDoesNotExist_Exception {
        if (repositories.get(userId) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(userId);
            throw new UserDoesNotExist_Exception("The user with the userId " + userId + " does not exist", udne);
        }
        return repositories.get(userId).listDocs();
    }

    @Override
    public void store(DocUserPair docUserPair, byte[] contents) throws CapacityExceeded_Exception, DocDoesNotExist_Exception,
            UserDoesNotExist_Exception {
        if (repositories.get(docUserPair.getUserId()) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(docUserPair.getUserId());
            throw new UserDoesNotExist_Exception("The user with the userId " + docUserPair.getUserId() + " does not exist", udne);
        }
        MessageContext context = webServiceContext.getMessageContext();
/*
        String authenticationHeaderString = (String) context.get("authenticationHeader");
        if (!authenticate(authenticationHeaderString))
            throw new CapacityExceeded_Exception("", null); //FIXME get proper exception
*/
        if (context.get("newTag") != null) {
            if ((int) context.get("newTag") >= repositories.get(docUserPair.getUserId()).getTag(docUserPair.getDocumentId())) {
                repositories.get(docUserPair.getUserId()).setTag(docUserPair.getDocumentId(), (int) context.get("newTag"));
                repositories.get(docUserPair.getUserId()).store(docUserPair.getDocumentId(), contents);
                context.put("ack", true);
                return;
            } else
                return;
        }
    }

    @Override
    public byte[] load(DocUserPair docUserPair) throws DocDoesNotExist_Exception, UserDoesNotExist_Exception {
        if (repositories.get(docUserPair.getUserId()) == null) {
            UserDoesNotExist udne = new UserDoesNotExist();
            udne.setUserId(docUserPair.getUserId());
            throw new UserDoesNotExist_Exception("The user with the userId " + docUserPair.getUserId() + " does not exist", udne);
        }
        MessageContext context = webServiceContext.getMessageContext();
        if (context.get("requestTag") != null && (boolean) context.get("requestTag")) {
            context.put("tag", repositories.get(docUserPair.getUserId()).getTag(docUserPair.getDocumentId()));
        }
        return repositories.get(docUserPair.getUserId()).load(docUserPair.getDocumentId());
    }

    private boolean authenticate(String chipheredTicket) {
        byte[] ticketBytes = null;

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            return false; //FIXME throw exception
        }

        byte[] secretServerKey = "SecretSecretSecretSecretSecretSecretSecret".getBytes();

        SecretKeyFactory factory;
        SecretKey key = null;
        try {
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(new DESKeySpec(secretServerKey));
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e1) {
            return false; //FIXME throw exception
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            ticketBytes = cipher.doFinal(parseBase64Binary(chipheredTicket));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            return false; //FIXME throw exception
        }

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);
        Document ticket = null;

        try {
            ticket = builder.build(new ByteArrayInputStream(ticketBytes));
        } catch (JDOMException | IOException e) {
            return false; //FIXME throw exception
        }

        DateTime expire = new DateTime(ticket.getRootElement().getChildText("Expiration"));
        DateTime startTime = new DateTime(ticket.getRootElement().getChildText("StartTime"));
        DateTime now = new DateTime();

        if (startTime.getMillis() >= now.getMillis() || expire.getMillis() <= now.getMillis())
            return false;

        clientServerKey = ticket.getRootElement().getChildText("Key");

        // System.out.println(clientServerKey);

        return true;
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
