package pt.tecnico.bubbledocs.integration.component;

import mockit.Mock;
import mockit.MockUp;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.CannotStoreDocumentException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.NonExistingSpreadsheetException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.integration.ExportDocumentIntegrator;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegratorTest extends BubbleDocsIntegratorTest {
    private final String NAME = "namelastname";
    private final String EMAIL = "namelastname@example.com";
    private final String USERNAME = "lastname";
    private final String PASSWORD = "password";
    private final String SS_NAME = "ssname";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";

    private String authorizedToken;
    private int validId;

    private static final int VALUE = 2;
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int CELL_1_COL = 1;
    private static final int CELL_1_ROW = 1;
    private static final int CELL_2_COL = 2;
    private static final int CELL_2_ROW = 2;

    //token nulo
    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new ExportDocumentIntegrator(null, validId).execute();
    }

    //Token invalido
    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new ExportDocumentIntegrator("", validId).execute();
    }

    //user nao esta em sessao
    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new ExportDocumentIntegrator(NOT_IN_SESSION_TOKEN, validId).execute();
    }

    //token expirado
    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(authorizedToken);
        new ExportDocumentIntegrator(authorizedToken, validId).execute();
    }

    //docid invalido
    @Test(expected = NonExistingSpreadsheetException.class)
    public void invalidDocId() {
        expireToken(authorizedToken);
        new ExportDocumentIntegrator(authorizedToken, 99999).execute();
    }

    @Test(expected = UnavailableServiceException.class)
    public void testRemoteInvocationException() {
        new MockUp<StoreRemoteServices>() {

            @Mock
            public void $init() {
            }

            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
                throw new RemoteInvocationException();
            }
        };

        ExportDocumentIntegrator service = new ExportDocumentIntegrator(authorizedToken, validId);
        service.execute();

    }

    @Test(expected = CannotStoreDocumentException.class)
    public void testCannotStoreDocumentException() {
        new MockUp<StoreRemoteServices>() {

            @Mock
            public void $init() {
            }

            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
                throw new CannotStoreDocumentException();
            }
        };

        ExportDocumentIntegrator service = new ExportDocumentIntegrator(authorizedToken, validId);
        service.execute();
    }

    @Test
    public void testStoreDocument() {
        new MockUp<StoreRemoteServices>() {

            @Mock
            public void $init() {
            }

            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
            }
        };
        ExportDocumentIntegrator service = new ExportDocumentIntegrator(authorizedToken, validId);
        service.execute();
    }

    @Override
    public void populate4Test() {
        createUser(USERNAME, PASSWORD, EMAIL, NAME);
        authorizedToken = addUserToSession(USERNAME);

        BubbleDocs bd = BubbleDocs.getInstance();

        Spreadsheet ss = createSpreadSheet(bd.getUserByUsername(USERNAME), SS_NAME, ROWS, COLS);
        validId = ss.getId();
        Cell c1 = new Cell(ss, CELL_1_ROW, CELL_1_COL);
        Cell c2 = new Cell(ss, CELL_2_ROW, CELL_2_COL);
        c1.setContent(new Literal(VALUE));
        c2.setContent(new Addition(new Reference(c1), new Literal(VALUE)));

        ss.addCells(c1);
        ss.addCells(c2);
    }
}
