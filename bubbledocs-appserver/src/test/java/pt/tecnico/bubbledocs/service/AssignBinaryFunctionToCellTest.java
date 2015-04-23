package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.DivisionByZeroException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.local.AssignBinaryFunctionToCell;

public class AssignBinaryFunctionToCellTest extends BubbleDocsServiceTest {

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
    private static final String REFERENCE_CELL = "2;2";
    private static final String REFERENCE_ZERO_CELL = "4;4";
    private static final String EMPTY_CELL = "1;2";
    private static final String FUNCTION_CELL = "3;3";
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
        Cell c3 = new Cell(ss, 3, 3);
        Cell c4 = new Cell(ss, 4, 4);
        Cell c5 = new Cell(ss, 5, 5);
        Cell c6 = new Cell(ss, 1, 2);
        ss.addCells(c6);
        c1.setContent(new Literal(VALUE));
        ss.addCells(c1);
        c2.setContent(new Reference(c2));
        ss.addCells(c2);
        c4.setContent(new Literal(0));
        ss.addCells(c4);
        c5.setContent(new Reference(c4));
        ss.addCells(c5);
        ss.addCells(c3);
    }

    @Test
    public void literalSuccess() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();

        Cell c = getSpreadSheet(SPREADSHEET_NAME).findCell(3, 3);

        assertNotNull(c.getContent());
        assertEquals(Addition.class, c.getContent().getClass());
        assertEquals(1 + 2, c.getContent().getValue().intValue());
    }

    @Test
    public void literalReferenceSuccess() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "SUB(50," + REFERENCE_CELL + ")");
        service.execute();

        Cell c = getSpreadSheet(SPREADSHEET_NAME).findCell(3, 3);

        assertNotNull(c.getContent());
        assertEquals(Addition.class, c.getContent().getClass());
        assertEquals(50 - 42, c.getContent().getValue().intValue());
    }

    @Test
    public void referenceReferenceSuccess() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "DIV(" + REFERENCE_CELL + "," + REFERENCE_CELL + ")");
        service.execute();

        Cell c = getSpreadSheet(SPREADSHEET_NAME).findCell(3, 3);

        assertNotNull(c.getContent());
        assertEquals(Addition.class, c.getContent().getClass());
        assertEquals(42 / 42, c.getContent().getValue().intValue());
    }

    @Test(expected = DivisionByZeroException.class)
    public void divisionByZeroTest() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "DIV(2,0)");
        service.execute();
    }

    @Test(expected = DivisionByZeroException.class)
    public void divisionByZeroReferenceTest() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "DIV(2," + REFERENCE_ZERO_CELL + ")");
        service.execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedToken() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(notOwnerToken, id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = UnauthorizedOperationException.class)
    public void readOnlyUser() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(readToken, id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void absentUser() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell("invalid", id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidRow() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(notOwnerToken, id, (ROWS + 1) + ";" + COLUMNS, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = CellOutOfRangeException.class)
    public void invalidColumn() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(notOwnerToken, id, ROWS + ";" + (COLUMNS + 1), "ADD(1,2)");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void invalidExpression() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "qwerty");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(null, id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell("", id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyCell() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, "", "ADD(1,2)");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullCell() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, null, "ADD(1,2)");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyExpression() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "");
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullExpression() {
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, null);
        service.execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(token);
        AssignBinaryFunctionToCell service = new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "ADD(1,2)");
        service.execute();
    }

    @Test
    public void emptyCellArgument() {
        AssignBinaryFunctionToCell service =
                new AssignBinaryFunctionToCell(token, id, FUNCTION_CELL, "ADD(1," + EMPTY_CELL + ")");
        service.execute();

        assertEquals("#VALUE", service.getResult());
    }

}
