package pt.tecnico.bubbledocs.integration.component;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Ignore;
import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.DuplicateEmailException;
import pt.tecnico.bubbledocs.exception.DuplicateUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyUsernameException;
import pt.tecnico.bubbledocs.exception.EmptyValueException;
import pt.tecnico.bubbledocs.exception.InvalidEmailException;
import pt.tecnico.bubbledocs.exception.InvalidUsernameException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integration.CreateUserIntegrator;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class CreateUserIntegratorTest extends BubbleDocsIntegratorTest {
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

    @Ignore
    @Test
    public void success() {
        CreateUserIntegrator service =
                new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        new Expectations() {
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
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        new Expectations() {
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
        CreateUserIntegrator service = new CreateUserIntegrator(root, "", EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    // appserver
    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedUserCreation() {
        CreateUserIntegrator service =
                new CreateUserIntegrator(ars, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    // appserver
    @Test(expected = UserNotInSessionException.class)
    public void accessUsernameNotExist() {
        removeUserFromSession(root);
        CreateUserIntegrator service =
                new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, "José Ferreira");
        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void shortUsername() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, SMALL_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);
        new Expectations() {
            {
                idRemoteServices.createUser(SMALL_USERNAME, EMAIL_DOES_NOT_EXIST);
            }
        };

        service.execute();
    }

    @Test(expected = InvalidUsernameException.class)
    public void longUsername() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, LONG_USERNAME, EMAIL_DOES_NOT_EXIST, NAME);
        new Expectations() {
            {
                idRemoteServices.createUser(LONG_USERNAME, EMAIL_DOES_NOT_EXIST);
            }
        };

        service.execute();
    }

    @Test(expected = DuplicateEmailException.class)
    public void emailExists() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL, NAME);
        new Expectations() {
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
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, "", NAME);
        service.execute();
    }

    // email structure: [a-z0-9]+@[a-z0-9]+.[a-z]+
    @Test(expected = InvalidEmailException.class)
    public void invalidEmail() {
        CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, "$$aa@x%.com", NAME);
        new Expectations() {
            {
                idRemoteServices.createUser(USERNAME_DOES_NOT_EXIST, "$$aa@x%.com");
                result = new InvalidEmailException();
            }
        };

        service.execute();
    }

    @Test
    public void remoteException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.createUser(USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST);
                result = new RemoteInvocationException();
            }
        };

        try {
            CreateUserIntegrator service = new CreateUserIntegrator(root, USERNAME_DOES_NOT_EXIST, EMAIL_DOES_NOT_EXIST, NAME);
            service.execute();
            fail("Expected UnavailableServiceException");
        } catch (UnavailableServiceException use) {
            User user = getUserFromUsername(USERNAME_DOES_NOT_EXIST);
            assertNull(user);
        }
    }
}
