package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertNull;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPasswordTest extends BubbleDocsServiceTest {
    private final String NAME = "the name";
    private final String USERNAME = "user25";
    private final String PASSWORD = "thepassword";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";
    private String USER_TOKEN;

    @Mocked
    IDRemoteServices idRemoteServices;

    @Override
    public void populate4Test() {
        createUser(USERNAME, "", NAME);
        USER_TOKEN = addUserToSession(USERNAME);
    }

    @Test
    public void success() {
        RenewPassword service = new RenewPassword(USER_TOKEN);

        new Expectations() {
            {
                idRemoteServices.renewPassword(USERNAME);
            }
        };

        service.execute();

        User user = getUserFromSession(USER_TOKEN);
        assertNull(user.getPassword());
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new RenewPassword(null).execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new RenewPassword("").execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new RenewPassword(NOT_IN_SESSION_TOKEN).execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(USER_TOKEN);
        new RenewPassword(USER_TOKEN).execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void remoteException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.renewPassword(USERNAME);
                result = new RemoteInvocationException();
            }
        };
        new RenewPassword(USER_TOKEN).execute();
    }
}
