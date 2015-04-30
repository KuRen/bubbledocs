package pt.tecnico.bubbledocs.service.local;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class AssignReferenceCellTest extends BubbleDocsServiceTest {

    private String token;
    private String notOwnerToken;
    private int id;
    private String readToken;

    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String EMAIL = "rito.silva@tecnico.ulisboa.pt";
    private static final String NOT_OWNER_USERNAME = "src";
    private static final String NOT_OWNER_PASSWORD = "src";
    private static final String SPREADSHEET_NAME = "ss-name";
    private static final String CELL = "2;2";
    private static final String REFERENCED_CELL = "1;1";
    private static final String UNEXISTING_CELL = "3;4";
    private static final int COLUMNS = 5;
    private static final int ROWS = 5;
    private static final Integer VALUE = 42;
    private static final String READER_USER = "rUser";

    @Override
    public void populate4Test() {
        BubbleDocs bd = BubbleDocs.getInstance();

        createUser(NOT_OWNER_USERNAME, NOT_OWNER_PASSWORD, EMAIL, "António Rito Silva");
        notOwnerToken = addUserToSession(NOT_OWNER_USERNAME);

        createUser(READER_USER, "password", "olo@yahoo.fr", "yet a bigger bigger name");
        readToken = addUserToSession(READER_USER);

        User ars = createUser(USERNAME, PASSWORD, EMAIL, "António Rito Silva");
        token = addUserToSession(USERNAME);

        Spreadsheet ss = createSpreadSheet(ars, SPREADSHEET_NAME, ROWS, COLUMNS);
        id = ss.getId();
        Cell c1 = new Cell(ss, 1, 1);
        Cell c2 = new Cell(ss, 2, 2);
        c1.setContent(new Literal(VALUE));
        ss.addCells(c1);
        ss.addCells(c2);
    }

    @Test
    public void success() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, REFERENCED_CELL);
        service.execute();

        Cell c1 = getSpreadSheet(SPREADSHEET_NAME).findCell(1, 1);
        Cell c2 = getSpreadSheet(SPREADSHEET_NAME).findCell(2, 2);

        assertNotNull(c2.getContent());
        assertEquals(Reference.class, c2.getContent().getClass());
        assertEquals(c1.getContent().getValue(), c2.getContent().getValue());
        assertTrue(c2.getContent().getValue() == VALUE);
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedToken() {
        AssignReferenceCell service = new AssignReferenceCell(notOwnerToken, id, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test
    public void unexistingCell() {
        new AssignReferenceCell(token, id, UNEXISTING_CELL, REFERENCED_CELL).execute();

        Cell c1 = getSpreadSheet(SPREADSHEET_NAME).findCell(1, 1);
        Cell c2 = getSpreadSheet(SPREADSHEET_NAME).findCell(3, 4);

        assertNotNull(c2.getContent());
        assertEquals(Reference.class, c2.getContent().getClass());
        assertEquals(c1.getContent().getValue(), c2.getContent().getValue());
        assertTrue(c2.getContent().getValue() == VALUE);
    }

    @Test
    public void unexistingReferencedCell() {
        new AssignReferenceCell(token, id, CELL, "1;2").execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void readOnlyUser() {
        AssignLiteralCell service = new AssignLiteralCell(readToken, id, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void absentUser() {
        AssignReferenceCell service = new AssignReferenceCell("invalid", id, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidCellRow() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, (ROWS + 1) + ";" + COLUMNS, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidCellColumn() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, ROWS + ";" + (COLUMNS + 1), REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void invalidArgumentCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, "qwerty", REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void invalidArgumentReferencedCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, "qwerty");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidReferencedCellRow() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, (ROWS + 1) + ";" + COLUMNS);
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidReferencedCellColumn() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, ROWS + ";" + (COLUMNS + 1));
        service.execute();
    }

    @Test(expected = InvalidSpreadSheetIdException.class)
    public void invalidSpreadSheetId() {
        AssignReferenceCell service = new AssignReferenceCell(token, -1, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        AssignReferenceCell service = new AssignReferenceCell("", id, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        AssignReferenceCell service = new AssignReferenceCell(null, id, CELL, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, "", REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, null, REFERENCED_CELL);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyReferencedCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, "");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullReferencedCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, null);
        service.execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(token);
        AssignReferenceCell service = new AssignReferenceCell(token, id, CELL, REFERENCED_CELL);
        service.execute();
    }
}
