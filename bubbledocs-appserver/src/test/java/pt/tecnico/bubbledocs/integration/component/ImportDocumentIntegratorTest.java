package pt.tecnico.bubbledocs.integration.component;

import org.junit.Assert;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.CannotLoadDocumentException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integration.ImportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocumentIntegratorTest extends BubbleDocsIntegratorTest {
    
    private final String NAME = "namelastname";
    private final String EMAIL = "namelastname@example.com";
    private final String USERNAME = "lastname";
    private final String PASSWORD = "password";
    private final String SS_NAME = "A SS Name";
    private final String DOCID = "documento";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";
    
    private String authorizedToken;

    //token nulo
    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new ImportDocumentIntegrator(DOCID, null).execute();
    }

    //Token invalido
    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new ImportDocumentIntegrator(DOCID, "").execute();
    }

    //user nao esta em sessao
    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new ImportDocumentIntegrator(DOCID, NOT_IN_SESSION_TOKEN).execute();
    }

    //token expirado
    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(authorizedToken);
        new ImportDocumentIntegrator(DOCID, authorizedToken).execute();
    }
    
    @Test(expected = UnavailableServiceException.class)
    public void testRemoteInvocationException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                throw new RemoteInvocationException();
            }
        };

        ImportDocumentIntegrator service = new ImportDocumentIntegrator(DOCID, authorizedToken);
        service.execute();

    }

    @Test(expected = CannotLoadDocumentException.class)
    public void testCannotLoadDocumentException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                throw new CannotLoadDocumentException();
            }
        };

        ImportDocumentIntegrator service = new ImportDocumentIntegrator(DOCID, authorizedToken);
        service.execute();
    }

    @Test
    public void testLoadDocument() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public byte[] loadDocument(String username, String docName) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Spreadsheet rows=\"1\" columns=\"1\" id=\"99\" name=\"A SS Name\" owner=\"lastname\" created=\"2015-04-24T20:25:06.747+01:00\"><Cells><Cell row=\"1\" column=\"1\"><Literal literal=\"2\" /></Cell></Cells></Spreadsheet>".getBytes();
            }
        };
        ImportDocumentIntegrator service = new ImportDocumentIntegrator(DOCID, authorizedToken);
        service.execute();
        Spreadsheet ss = getSpreadSheet(SS_NAME);
        Assert.assertEquals(ss.getOwner().getUsername(), USERNAME);
        Assert.assertEquals(ss.getColumns(), new Integer(1));
        Assert.assertEquals(ss.getRows(), new Integer(1));
    }

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, NAME);
        authorizedToken = addUserToSession(USERNAME);
    }
}
