package pt.tecnico.bubbledocs.integration.component;

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
import pt.tecnico.bubbledocs.integration.ExportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegratorTest extends BubbleDocsIntegratorTest {
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
    StoreRemoteServices storeRemoteServices;

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, NAME);
        USER_TOKEN = addUserToSession(USERNAME);
    }

    @Test
    public void success() {
        ExportDocumentIntegrator service = new ExportDocumentIntegrator(USER_TOKEN, DOC_ID);

        new Expectations() {
            {
                storeRemoteServices.storeDocument(USERNAME, SS_NAME, service.getDocXML());
            }
        };

        service.execute();

    }

    //token nulo
    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new ExportDocumentIntegrator(null, DOC_ID).execute();
    }

    //Token invalido
    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new ExportDocumentIntegrator("", DOC_ID).execute();
    }

    //user nao esta em sessao
    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new ExportDocumentIntegrator(NOT_IN_SESSION_TOKEN, DOC_ID).execute();
    }

    //token expirado
    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(USER_TOKEN);
        new ExportDocumentIntegrator(USER_TOKEN, DOC_ID).execute();
    }

    @Test
    public void remoteException() {
        ExportDocumentIntegrator service = new ExportDocumentIntegrator(USER_TOKEN, DOC_ID);
        new NonStrictExpectations() {
            {
                storeRemoteServices.storeDocument(USERNAME, SS_NAME, service.getDocXML());
                result = new RemoteInvocationException();
            }
        };
        try {
            fail("Expected UnavailableServiceException");
        } catch (UnavailableServiceException use) {
        }
    }
}
