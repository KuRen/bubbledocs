package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NotLiteralException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class AssignLiteralCellTest extends BubbleDocsServiceTest {

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
    private static final String CELL = "1;1";
    private static final String VALUE = "42";

    @Override
    public void populate4Test() {
        createUser(NOT_OWNER_USERNAME, NOT_OWNER_PASSWORD, "António Rito Silva");
        notOwnerToken = addUserToSession(NOT_OWNER_USERNAME);

        User ars = createUser(USERNAME, PASSWORD, "António Rito Silva");
        token = addUserToSession(USERNAME);

        Spreadsheet ss = createSpreadSheet(ars, SPREADSHEET_NAME, ROWS, COLUMNS);
        id = ss.getId();

        Cell c1 = new Cell(ss, 1, 1);

        ss.addCells(c1);
    }

    @Test
    public void success() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, CELL, VALUE);
        service.execute();

        Cell c1 = getSpreadSheet(SPREADSHEET_NAME).findCell(1, 1);

        assertNotNull(c1.getContent());
        assertEquals(c1.getContent().getClass().getSimpleName(), "Literal");
        assertTrue(Integer.parseInt(VALUE) == c1.getContent().getValue());
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedToken() {
        AssignLiteralCell service = new AssignLiteralCell(notOwnerToken, id, CELL, VALUE);
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void absentUser() {
        AssignLiteralCell service = new AssignLiteralCell("invalid", id, CELL, VALUE);
        service.execute();
    }

    @Test(expected = NotLiteralException.class)
    public void invalidType2() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, CELL, "-2.3");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void invalidArgumentCell() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "qwerty", VALUE);
        service.execute();
    }

    @Test(expected = NotLiteralException.class)
    public void invalidType() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, CELL, "qwerty");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void outOfRange() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "8;8", VALUE);
        service.execute();
    }

    @Test(expected = InvalidSpreadSheetIdException.class)
    public void invalidSpreadSheetId() {
        AssignLiteralCell service = new AssignLiteralCell(token, 0, CELL, VALUE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        AssignLiteralCell service = new AssignLiteralCell("", id, CELL, VALUE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyCell() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "", VALUE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyReferencedCell() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, CELL, "");
        service.execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(token);
        AssignLiteralCell service = new AssignLiteralCell(token, id, CELL, VALUE);
        service.execute();
    }
}