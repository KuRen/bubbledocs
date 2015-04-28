package pt.tecnico.bubbledocs.integration;

import static org.junit.Assert.fail;
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.Test;

import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class ExportDocumentServiceIntegrationTest extends BubbleDocsIntegrationTest {
    private final String NAME = "namelastname";
    private final String EMAIL = "namelastname@example.com";
    private final String USERNAME = "usernamelastname";
    private final String PASSWORD = "password";
    private final String SS_NAME = "ssname";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";
    private final Integer DOC_ID = 1;
    private String USER_TOKEN;

    @Mocked
    IDRemoteServices idRemoteServices;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, NAME);
        USER_TOKEN = addUserToSession(USERNAME);
    }

    @Test
    public void success() {
        ExportDocumentIntegration service = new ExportDocumentIntegration(USER_TOKEN, DOC_ID);

        new Expectations() {
            {
                idRemoteServices.storeDocument(USERNAME, SS_NAME, service.getDocXML());
            }
        };

        service.execute();

    }

    //token nulo
    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new ExportDocumentIntegration(null, DOC_ID).execute();
    }

    //Token invalido
    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new ExportDocumentIntegration("", DOC_ID).execute();
    }

    //user nao esta em sessao
    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new ExportDocumentIntegration(NOT_IN_SESSION_TOKEN, DOC_ID).execute();
    }

    //token expirado
    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(USER_TOKEN);
        new ExportDocumentIntegration(USER_TOKEN, DOC_ID).execute();
    }

    @Test
    public void remoteException() {
        new NonStrictExpectations() {
            {
                idRemoteServices.storeDocument(USERNAME, SS_NAME, service.getDocXML());
                result = new RemoteInvocationException();
            }
        };
        try {
            fail("Expected UnavailableServiceException");
        } catch (UnavailableServiceException use) {
        }
    }
}
