package pt.ulisboa.tecnico.sdis.id.ws.impl;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed;
import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class UserManager {

    private static UserManager instance = null;
    private Map<String, User> users = new HashMap<String, User>();

    protected UserManager() {

    }

    public static UserManager getInstance() {
        if (instance == null)
            instance = new UserManager();
        return instance;
    }

    public User create(String username, String email, String password) throws EmailAlreadyExists_Exception,
            InvalidEmail_Exception, InvalidUser_Exception, UserAlreadyExists_Exception {
        validateUsername(username);
        validateEmail(email);
        if (password == null) {
            password = generatePassword();
        }
        User user = new User(username, password, email);
        users.put(username, user);
        return user;
    }

    private String generatePassword() {
        char[] characters =
                new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                        'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
                        'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        return randomString(characters, 8);
    }

    private String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    public User create(String username, String email) throws EmailAlreadyExists_Exception, InvalidEmail_Exception,
            InvalidUser_Exception, UserAlreadyExists_Exception {
        return create(username, email, null);
    }

    private void validateEmail(String email) throws InvalidEmail_Exception, EmailAlreadyExists_Exception {
        if (email == null || !email.matches("[\\w\\._]+@(\\w+\\.)*\\w+\\.\\w+")) {
            InvalidEmail faultInfo = new InvalidEmail();
            faultInfo.setEmailAddress(email);
            throw new InvalidEmail_Exception("Invalid email: " + email, faultInfo);
        }
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                EmailAlreadyExists faultInfo = new EmailAlreadyExists();
                faultInfo.setEmailAddress(email);
                throw new EmailAlreadyExists_Exception("Email " + email + " already exists", faultInfo);
            }
        }
    }

    private void validateUsername(String username) throws InvalidUser_Exception, UserAlreadyExists_Exception {
        if (username == null || !username.matches(".+")) {
            InvalidUser faultInfo = new InvalidUser();
            faultInfo.setUserId(username);
            throw new InvalidUser_Exception("Invalid username: " + username, faultInfo);
        }
        if (users.get(username) != null) {
            UserAlreadyExists faultInfo = new UserAlreadyExists();
            faultInfo.setUserId(username);
            throw new UserAlreadyExists_Exception("User " + username + " already exists", faultInfo);
        }
    }

    public User renewPassword(String username, String password) throws UserDoesNotExist_Exception {
        User user = users.get(username);
        if (user == null) {
            UserDoesNotExist faultInfo = new UserDoesNotExist();
            faultInfo.setUserId(username);
            throw new UserDoesNotExist_Exception("User does not exist: " + username, faultInfo);
        }
        if (password == null) {
            password = generatePassword();
        }
        user.setPassword(password);
        return user;
    }

    public User renewPassword(String username) throws UserDoesNotExist_Exception {
        return renewPassword(username, null);
    }

    public void remove(String username) throws UserDoesNotExist_Exception {
        User user = users.get(username);
        if (user == null) {
            UserDoesNotExist faultInfo = new UserDoesNotExist();
            faultInfo.setUserId(username);
            throw new UserDoesNotExist_Exception("User does not exist: " + username, faultInfo);
        }
        users.remove(username);
    }

    public void authenticate(String username, byte[] reserved) throws AuthReqFailed_Exception {
        User user = null;
        try {
            user = getUserByName(username);
        } catch (UserDoesNotExist_Exception e) {
            throwAuthFailedException(reserved);
        }

        if (reserved == null) {
            throwAuthFailedException(reserved);
        }

        String password = new String(reserved);

        if (!user.getPassword().equals(password)) {
            throwAuthFailedException(reserved);
        }
    }

    private void throwAuthFailedException(byte[] reserved) throws AuthReqFailed_Exception {
        AuthReqFailed faultInfo = new AuthReqFailed();
        faultInfo.setReserved(reserved);
        throw new AuthReqFailed_Exception("Authentication failed.", faultInfo);
    }

    public int size() {
        return users.size();
    }

    public User getUserByName(String username) throws UserDoesNotExist_Exception {
        if (username == null || username.isEmpty()) {
            throw new UserDoesNotExist_Exception(username, null);
        }

        User user = users.get(username);

        if (user == null) {
            throw new UserDoesNotExist_Exception(username, null);
        }

        return user;
    }
}
