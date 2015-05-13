package pt.ulisboa.tecnico.sdis.id.ws.impl;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
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

        System.out.println("Going for ciphers!!");
        String kcsString = null;
        try {
            kcsString = generateKCS();
        } catch (NoSuchAlgorithmException e2) {
            throw new AuthReqFailed_Exception(userId, null);
        }

        try {
            System.out.println(cipherXMLForServer(generateTicket(userId, server, kcsString)));
            ticketElement.setText(cipherXMLForServer(generateTicket(userId, server, kcsString)));
            KcsNoncePairElement.setText(cipherXMLForClient(generateClientServerKey(nonce, kcsString), user.getPassword()));
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException | IllegalBlockSizeException
                | BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException
                | InvalidAlgorithmParameterException e1) {

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
            UnsupportedEncodingException, InvalidAlgorithmParameterException {
        byte[] bytes = xml.getBytes();
        // generate a secret key
        MessageDigest cript = MessageDigest.getInstance("SHA-1");
        cript.reset();
        cript.update(password.getBytes("UTF8"));
        byte[] hash = cript.digest();
        hash = Arrays.copyOf(hash, 16);
        //SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        //byte[] salt = "saltsalt".getBytes();
        //SecretKey key = factory.generateSecret(new PBEKeySpec(password.toCharArray(), salt, 65536, 128));

        Key key = new SecretKeySpec(hash, "AES");

        // get a AES cipher object
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // encrypt using the key and the plaintext
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] cipherBytes = cipher.doFinal(bytes);

        return printBase64Binary(cipherBytes);
    }

    private String cipherXMLForServer(String xml) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        byte[] bytes = xml.getBytes();
        String secretServerKey = "SecretSecretSecr";
        // generate a secret key
        // SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        //byte[] salt = "saltsalt".getBytes();
        //SecretKey key = factory.generateSecret(new PBEKeySpec(secretServerKey.toCharArray(), salt, 65536, 128));

        Key key = new SecretKeySpec(secretServerKey.getBytes(), "AES");

        // get a AES cipher object
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // encrypt using the key and the plaintext
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(new byte[16]));
        byte[] cipherBytes = cipher.doFinal(bytes);

        return printBase64Binary(cipherBytes);
    }

    private String generateTicket(String userId, String server, String kcsString) throws NoSuchAlgorithmException {
        String kCS = kcsString;
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
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        Key key = keyGen.generateKey();

        System.out.println("Plain: " + new String(key.getEncoded()));
        System.out.println("Encoded: " + printBase64Binary(key.getEncoded()));

        return printBase64Binary(key.getEncoded());
    }

    private String generateClientServerKey(String nonce, String kcsString) throws NoSuchAlgorithmException {
        String kCS = kcsString;
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
