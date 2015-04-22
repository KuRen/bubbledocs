package pt.tecnico.bubbledocs.service;

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
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.ExportDocument;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentTest extends BubbleDocsServiceTest {

    private String authorizedToken;
    private int validId;

    private static final int VALUE = 2;
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int CELL_1_COL = 1;
    private static final int CELL_1_ROW = 1;
    private static final int CELL_2_COL = 2;
    private static final int CELL_2_ROW = 2;

    private static final String USER = "userName";
    private static final String SS_NAME = "A SS Name";

    @Test(expected = UnavailableServiceException.class)
    public void testRemoteInvocationException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
                throw new RemoteInvocationException();
            }
        };

        ExportDocument service = new ExportDocument(authorizedToken, validId);
        service.execute();

    }

    @Test(expected = CannotStoreDocumentException.class)
    public void testCannotStoreDocumentException() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
                throw new CannotStoreDocumentException();
            }
        };

        ExportDocument service = new ExportDocument(authorizedToken, validId);
        service.execute();
    }

    @Test
    public void testStoreDocument() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
            }
        };
        ExportDocument service = new ExportDocument(authorizedToken, validId);
        service.execute();
    }

    @Override
    public void populate4Test() {
        createUser(USER, "password", "email", "big complete name");
        authorizedToken = addUserToSession(USER);

        BubbleDocs bd = BubbleDocs.getInstance();

        Spreadsheet ss = createSpreadSheet(bd.getUserByUsername(USER), SS_NAME, ROWS, COLS);
        validId = ss.getId();
        Cell c1 = new Cell(ss, CELL_1_ROW, CELL_1_COL);
        Cell c2 = new Cell(ss, CELL_2_ROW, CELL_2_COL);
        c1.setContent(new Literal(VALUE));
        c2.setContent(new Addition(new Reference(c1), new Literal(VALUE)));

        ss.addCells(c1);
        ss.addCells(c2);
    }
}
