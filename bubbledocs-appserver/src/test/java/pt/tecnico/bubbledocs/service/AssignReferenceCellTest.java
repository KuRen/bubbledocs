package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class AssignReferenceCellTest extends BubbleDocsServiceTest {

    private String token;
    private String notOwnerToken;
    private int id;

    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String NOT_OWNER_USERNAME = "src";
    private static final String NOT_OWNER_PASSWORD = "src";
    private static final String SPREADSHEET_NAME = "ss-name";
    private static final int COLUMNS = 5;
    private static final int ROWS = 5;
    private static final Integer VALUE = 42;

    @Override
    public void populate4Test() {
        createUser(NOT_OWNER_USERNAME, NOT_OWNER_PASSWORD, "António Rito Silva");
        notOwnerToken = addUserToSession(NOT_OWNER_USERNAME);

        User ars = createUser(USERNAME, PASSWORD, "António Rito Silva");
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
        AssignReferenceCell service = new AssignReferenceCell(token, id, "2;2", "1;1");
        service.execute();

        Cell c1 = getSpreadSheet(SPREADSHEET_NAME).findCell(1, 1);
        Cell c2 = getSpreadSheet(SPREADSHEET_NAME).findCell(2, 2);

        assertNotNull(c2.getContent());
        assertEquals(c2.getContent().getClass().getSimpleName(), "Reference");
        assertEquals(c1.getContent().getValue(), c2.getContent().getValue());
        assertTrue(c2.getContent().getValue() == VALUE);
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedToken() {
        AssignReferenceCell service = new AssignReferenceCell(notOwnerToken, id, "2;2", "1;1");
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void absentUser() {
        AssignReferenceCell service = new AssignReferenceCell("invalid", id, "2;2", "1;1");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, "9;9", "1;1");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void invalidArgumentCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, "qwerty", "asdf");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidReferencedCell() {
        AssignReferenceCell service = new AssignReferenceCell(token, id, "2;2", "9;9");
        service.execute();
    }

    @Test(expected = InvalidSpreadSheetIdException.class)
    public void invalidSpreadSheetId() {
        AssignReferenceCell service = new AssignReferenceCell(token, 0, "2;2", "1;1");
        service.execute();
    }
}
