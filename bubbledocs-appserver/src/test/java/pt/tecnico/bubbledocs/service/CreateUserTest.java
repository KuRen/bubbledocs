package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyValueException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class CreateUserTest extends BubbleDocsServiceTest {

    // the tokens
    private String root;
    private String ars;

    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String EMAIL = "rito.silva@tecnico.ulisboa.pt";
    private static final String NAME = "John Doe";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";
    private static final String EMAIL_DOES_NOT_EXIST = "jose.ferreira@gmail.com";
    private static final String SMALL_USERNAME = "ab";
    private static final String LONG_USERNAME = "nineChars";

    @Mocked
    IDRemoteServices idRemoteServices;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, "António Rito Silva");
        root = addUserToSession("root");
        ars = addUserToSession("ars");
    }

    @Test
    public void success() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST);
            }
        };

        service.execute();

        // User is the domain class that represents a User
        User user = getUserFromUsername(USERNAME_DOES_NOT_EXIST);

        assertEquals(USERNAME_DOES_NOT_EXIST, user.getUsername());
        assertEquals("José Ferreira", user.getName());
        assertEquals(EMAIL_DOES_NOT_EXIST, user.getEmail());
    }

    @Test(expected = DuplicateUsernameException.class)
    public void usernameExists() {
        CreateUser service = new CreateUser(root, USERNAME, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(USERNAME, EMAIL_DOES_NOT_EXIST);
                result = new DuplicateUsernameException();
            }
        };

        service.execute();
    }

    // appserver
    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() {
        CreateUser service = new CreateUser(root, "", EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    // appserver
    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedUserCreation() {
        CreateUser service = new CreateUser(ars, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    // appserver
    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() {
        removeUserFromSession(root);
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void shortUsername() {
        CreateUser service = new CreateUser(root, SMALL_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(SMALL_USERNAME, EMAIL_DOES_NOT_EXIST);
                result = new InvalidUsernameException();
            }
        };

        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void longUsername() {
        CreateUser service = new CreateUser(root, LONG_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(LONG_USERNAME, EMAIL_DOES_NOT_EXIST);
                result = new InvalidUsernameException();
            }
        };

        service.execute();
    }

    @Test(expected = DuplicateEmailException.class)
    public void emailExists() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL, NAME);
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(USERNAME_DOES_NOT_EXIST, EMAIL);
                result = new DuplicateEmailException();
            }
        };

        service.execute();
    }

    // appserver
    @Test(expected = EmptyValueException.class)
    public void emptyEmail() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, "", NAME);
        service.execute();
    }

    // email structure: [a-z0-9]+@[a-z0-9]+.[a-z]+
    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, "$$aa@x%.com", NAME);
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(USERNAME_DOES_NOT_EXIST, "$$aa@x%.com");
                result = new InvalidEmailException();
            }
        };

        service.execute();
    }
}
