package pt.ulisboa.tecnico.sdis.id.client;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

import pt.ulisboa.tecnico.sdis.id.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.SDId;
import pt.ulisboa.tecnico.sdis.id.ws.SDId_Service;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClient implements SDId {
    /** WS service */
    SDId_Service service = null;

    /** WS port (interface) */
    SDId port = null;

    /** Endpoint URL */
    private String URL = null;

    /** output option **/
    private boolean verbose = false;

    /** server to make connection with **/
    private final String SERVER_NAME = "CentralServer";

    private String ticket = null;
    private String kCS = null;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    /**
     * constructor with provided web service URL
     * 
     * @param uddiURL
     * @param serviceName
     * @throws serviceFindException
     * 
     * @throws JAXRException
     */
    public IdClient(String uddiURL, String serviceName) throws serviceFindException {
        lookForService(uddiURL, serviceName);
        createStub();
    }

    private void createStub() {
        if (verbose)
            System.out.println("Creating stub ...");

        service = new SDId_Service();
        port = service.getSDIdImplPort();

        if (verbose)
            System.out.println("Setting endpoint address ...");

        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, URL);
    }

    private void lookForService(String uddiURL, String serviceName) throws serviceFindException {
        if (verbose)
            System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming;
        try {
            uddiNaming = new UDDINaming(uddiURL);

            if (verbose)
                System.out.printf("Looking for '%s'%n", serviceName);
            URL = uddiNaming.lookup(serviceName);

            if (URL == null && verbose) {
                System.out.println("Not found!");
                return;
            } else {
                if (verbose)
                    System.out.printf("Found %s%n", URL);
            }

        } catch (JAXRException e) {
            throw new serviceFindException();
        }
    }

    // SDId

    @Override
    public void createUser(String userId, String emailAddress) throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {

        port.createUser(userId, emailAddress);

    }

    @Override
    public void renewPassword(String userId) throws UserDoesNotExist_Exception {
        port.renewPassword(userId);

    }

    @Override
    public void removeUser(String userId) throws UserDoesNotExist_Exception {
        port.removeUser(userId);

    }

    @Override
    public byte[] requestAuthentication(String userId, byte[] reserved) throws AuthReqFailed_Exception {
        byte[] responseBytes = null;
        Document response;
        byte[] kCSNoncePairBytes = null;
        Document kCSNoncePair;
        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        String nonce = generateStringNonce();

        Document request = new Document();
        XMLOutputter xmlOutputter = new XMLOutputter();

        Element serverElement = new Element("Server");
        Element nonceElement = new Element("Nonce");
        Element rootElement = new Element("Request");

        serverElement.setText(SERVER_NAME);
        nonceElement.setText(nonce);

        rootElement.addContent(serverElement);
        rootElement.addContent(nonceElement);

        request.setRootElement(rootElement);

        try {
            responseBytes = port.requestAuthentication(userId, xmlOutputter.outputString(request).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new AuthReqFailed_Exception("Encoding error", null);
        }

        try {
            response = builder.build(new ByteArrayInputStream(responseBytes));
        } catch (JDOMException e1) {
            throw new AuthReqFailed_Exception(userId, null);
        } catch (IOException e1) {
            throw new AuthReqFailed_Exception(userId, null);
        }

        String kcsNoncePair = response.getRootElement().getChildText("KcsNoncePair");

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

        // generate a secret key
        SecretKey key = null;
        try {
            MessageDigest cript = MessageDigest.getInstance("SHA-1");
            cript.reset();
            cript.update(reserved);
            byte[] hash = cript.digest();
            SecretKeyFactory factory;
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(new DESKeySpec(hash));
        } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException e) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            kCSNoncePairBytes = cipher.doFinal(parseBase64Binary(kcsNoncePair));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

        try {
            kCSNoncePair = builder.build(new ByteArrayInputStream(kCSNoncePairBytes));
        } catch (JDOMException | IOException e) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

        if (!kCSNoncePair.getRootElement().getChildText("Nonce").equals(nonce)) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

        setkCS(kCSNoncePair.getRootElement().getChildText("Key"));
        setTicket(response.getRootElement().getChildText("Ticket"));

        response.getRootElement().getChild("KcsNoncePair").setText(getkCS());

        try {
            return xmlOutputter.outputString(response).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AuthReqFailed_Exception("Failed to login " + userId, null);
        }

    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getkCS() {
        return kCS;
    }

    public void setkCS(String kCS) {
        this.kCS = kCS;
    }

    private String generateStringNonce() {
        return Integer.toString(new SecureRandom().nextInt());
    }

}