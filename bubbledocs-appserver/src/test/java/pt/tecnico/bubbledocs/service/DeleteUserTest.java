package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UnknownBubbleDocsUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

// add needed import declarations

public class DeleteUserTest extends BubbleDocsServiceTest {

    private static final String USERNAME_TO_DELETE = "smf";
    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String ROOT_USERNAME = "root";
    private static final String USERNAME_DOES_NOT_EXIST = "no-one";
    private static final String SPREADSHEET_NAME = "spread";
    private static final String EMAIL = "rito.silva@tecnico.ulisboa.pt";
    private static final String EMAIL_ALREADY_TAKEN = "sergio.fernandes@tecnico.ulisboa.pt";

    @Mocked
    IDRemoteServices idRemoteServices;

    // the tokens for user root
    private String root;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, "António Rito Silva");
        User smf = createUser(USERNAME_TO_DELETE, "smf", EMAIL_ALREADY_TAKEN, "Sérgio Fernandes");
        createSpreadSheet(smf, USERNAME_TO_DELETE, 20, 20);

        root = addUserToSession(ROOT_USERNAME);
    };

    public void success() {
        mockRemoteRemoveUserMethod();
        DeleteUser service = new DeleteUser(root, USERNAME_TO_DELETE);
        service.execute();

        boolean deleted = getUserFromUsername(USERNAME_TO_DELETE) == null;

        assertTrue("user was not deleted", deleted);

        assertNull("Spreadsheet was not deleted", getSpreadSheet(SPREADSHEET_NAME));
    }

    /*
     * accessUsername exists, is in session and is root toDeleteUsername exists
     * and is not in session
     */
    @Test
    public void successToDeleteIsNotInSession() {
        success();
    }

    /*
     * accessUsername exists, is in session and is root toDeleteUsername exists
     * and is in session Test if user and session are both deleted
     */
    @Test
    public void successToDeleteIsInSession() {
        String token = addUserToSession(USERNAME_TO_DELETE);
        success();
        assertNull("Removed user but not removed from session", getUserFromSession(token));
    }

    @Test(expected = UnknownBubbleDocsUserException.class)
    public void userToDeleteDoesNotExist() {
        mockRemoteRemoveUserMethod();
        new DeleteUser(root, USERNAME_DOES_NOT_EXIST).execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void notRootUser() {
        mockRemoteRemoveUserMethod();
        String ars = addUserToSession(USERNAME);
        new DeleteUser(ars, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void rootNotInSession() {
        mockRemoteRemoveUserMethod();
        removeUserFromSession(root);
        new DeleteUser(root, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void notInSessionAndNotRoot() {
        mockRemoteRemoveUserMethod();
        String ars = addUserToSession(USERNAME);
        removeUserFromSession(ars);
        new DeleteUser(ars, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void accessUserDoesNotExist() {
        mockRemoteRemoveUserMethod();
        new DeleteUser(USERNAME_DOES_NOT_EXIST, USERNAME_TO_DELETE).execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void remoteException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.removeUser(USERNAME_TO_DELETE);
                result = new RemoteInvocationException();
            }
        };

        new DeleteUser(root, USERNAME_TO_DELETE).execute();
    }

    private void mockRemoteRemoveUserMethod() {
        new NonStrictExpectations() {
            {
                idRemoteServices.removeUser(USERNAME_TO_DELETE);
            }
        };
    }
}
