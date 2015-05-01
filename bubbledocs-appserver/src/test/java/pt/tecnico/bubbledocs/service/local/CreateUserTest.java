package pt.tecnico.bubbledocs.service.local;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyValueException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

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

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, "António Rito Silva");
        root = addUserToSession("root");
        ars = addUserToSession("ars");
    }

    @Test
    public void success() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");

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
        service.execute();
    }

    @Test(expected = EmptyUsernameException.class)
    public void emptyUsername() {
        CreateUser service = new CreateUser(root, "", EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedUserCreation() {
        CreateUser service = new CreateUser(ars, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() {
        removeUserFromSession(root);
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void shortUsername() {
        CreateUser service = new CreateUser(root, SMALL_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void longUsername() {
        CreateUser service = new CreateUser(root, LONG_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);

        service.execute();
    }

    @Test(expected = DuplicateEmailException.class)
    public void emailExists() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, EMAIL, NAME);

        service.execute();
    }

    @Test(expected = EmptyValueException.class)
    public void emptyEmail() {
        CreateUser service = new CreateUser(root, USERNAME_DOES_NOT_EXIST, "", NAME);
        service.execute();
    }
}
