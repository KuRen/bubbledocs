package pt.ulisboa.tecnico.sdis.id.ws.impl;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.jws.WebService;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.joda.time.DateTime;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

@SuppressWarnings("restriction")
@WebService(endpointInterface = "pt.ulisboa.tecnico.sdis.id.ws.SDId", wsdlLocation = "SD-ID.1_1.wsdl", name = "SdId",
        portName = "SDIdImplPort", targetNamespace = "urn:pt:ulisboa:tecnico:sdis:id:ws", serviceName = "SDId")
public class IdImpl implements SDId {

    private Boolean verbose = false;

    public Boolean getVerbose() {
        return verbose;
    }

    public void setVerbose(Boolean verbose) {
        this.verbose = verbose;
    }

    public IdImpl() {
        super();
        setVerbose(false);
    }

    @Override
    public void createUser(String userId, String emailAddress) throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        UserManager userManager = UserManager.getInstance();
        User user = userManager.create(userId, emailAddress);
        System.out.printf("[Create] User: %s | Email: %s | Password: %s%n", user.getUsername(), user.getEmail(),
                user.getPassword());
    }

    @Override
    public void renewPassword(String userId) throws UserDoesNotExist_Exception {
        UserManager userManager = UserManager.getInstance();
        User user = userManager.renewPassword(userId);
        System.out.printf("[ Renew] User: %s | Password: %s%n", user.getUsername(), user.getPassword());
    }

    @Override
    public void removeUser(String userId) throws UserDoesNotExist_Exception {
        UserManager userManager = UserManager.getInstance();
        userManager.remove(userId);
        System.out.printf("[Remove] User: %s%n", userId);
    }

    @Override
    public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception {
        if (userId == null || reserved == null) {
            throw new AuthReqFailed_Exception(userId, null);
        }

        UserManager userManager = UserManager.getInstance();
        User user = null;
        try {
            user = userManager.getUserByName(userId);
        } catch (UserDoesNotExist_Exception e2) {
            if (verbose) {
                e2.printStackTrace();
            }

            throw new AuthReqFailed_Exception(userId, null);
        }

        Document request;
        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        try {
            request = builder.build(new ByteArrayInputStream(reserved));
        } catch (JDOMException e1) {
            if (verbose) {
                e1.printStackTrace();
            }
            throw new AuthReqFailed_Exception(userId, null);
        } catch (IOException e1) {
            if (verbose) {
                e1.printStackTrace();
            }
            throw new AuthReqFailed_Exception(userId, null);
        }

        String server = request.getRootElement().getChildText("Server");
        String nonce = request.getRootElement().getChildText("Nonce");

        Document response = new Document();
        XMLOutputter xmlOutputter = new XMLOutputter();
        Element rootElement = new Element("Request");
        Element ticketElement = new Element("Ticket");
        Element KcsNoncePairElement = new Element("KcsNoncePair");

        try {
            ticketElement.setText(cipherXMLForServer(generateTicket(userId, server)));
            KcsNoncePairElement.setText(cipherXMLForClient(generateClientServerKey(nonce), user.getPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException
                | BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException e1) {

            if (verbose) {
                e1.printStackTrace();
            }
            throw new AuthReqFailed_Exception("Error generating ticket for: " + userId, null);
        }

        rootElement.addContent(ticketElement);
        rootElement.addContent(KcsNoncePairElement);
        response.setRootElement(rootElement);

        System.out.printf("[  Auth] User: %s%n", userId);
        try {
            return xmlOutputter.outputString(response).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            if (verbose) {
                e.printStackTrace();
            }
            throw new AuthReqFailed_Exception(userId, null);
        }
    }

    private String cipherXMLForClient(String xml, String password) throws NoSuchAlgorithmException, InvalidKeyException,
            InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException,
            UnsupportedEncodingException {
        byte[] bytes = xml.getBytes();
        // generate a secret key
        MessageDigest cript = MessageDigest.getInstance("SHA-1");
        cript.reset();
        cript.update(password.getBytes("UTF8"));
        byte[] hash = cript.digest();
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey key = factory.generateSecret(new DESKeySpec(hash));

        // get a DES cipher object
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        // encrypt using the key and the plaintext
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(bytes);

        return printBase64Binary(cipherBytes);
    }

    private String cipherXMLForServer(String xml) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        byte[] bytes = xml.getBytes();
        byte[] secretServerKey = "SecretSecretSecretSecretSecretSecretSecret".getBytes();
        // generate a secret key
        SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
        SecretKey key = factory.generateSecret(new DESKeySpec(secretServerKey));

        // get a DES cipher object
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");

        // encrypt using the key and the plaintext
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(bytes);

        return printBase64Binary(cipherBytes);
    }

    private String generateTicket(String userId, String server) throws NoSuchAlgorithmException {
        String kCS = generateKCS(); // TODO
        DateTime now = new DateTime();
        int SESSION_TIME = 2;

        Document ticket = new Document();
        XMLOutputter xmlOutputter = new XMLOutputter();

        Element rootElement = new Element("CipheredPackage");
        Element clientElement = new Element("Client");
        Element serverElement = new Element("Server");
        Element startTimeElement = new Element("StartTime");
        Element expirationElement = new Element("Expiration");
        Element kCSElement = new Element("Key");

        clientElement.setText(userId);
        serverElement.setText(server);
        startTimeElement.setText(now.toString());
        expirationElement.setText(now.plusHours(SESSION_TIME).toString());
        kCSElement.setText(kCS);

        rootElement.addContent(clientElement);
        rootElement.addContent(serverElement);
        rootElement.addContent(startTimeElement);
        rootElement.addContent(expirationElement);
        rootElement.addContent(kCSElement);

        ticket.setRootElement(rootElement);

        return xmlOutputter.outputString(ticket);

    }

    private String generateKCS() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(56);
        Key key = keyGen.generateKey();
        return printBase64Binary(key.getEncoded());
    }

    private String generateClientServerKey(String nonce) {
        String kCS = "SomeKeyAplha42"; // TODO
        Document KcsNoncePair = new Document();
        XMLOutputter xmlOutputter = new XMLOutputter();

        Element rootElement = new Element("CipheredPackage");
        Element kCSElement = new Element("Key");
        Element nonceElement = new Element("Nonce");

        kCSElement.setText(kCS);
        nonceElement.setText(nonce);
        rootElement.addContent(kCSElement);
        rootElement.addContent(nonceElement);
        KcsNoncePair.setRootElement(rootElement);

        return xmlOutputter.outputString(KcsNoncePair);
    }
}
