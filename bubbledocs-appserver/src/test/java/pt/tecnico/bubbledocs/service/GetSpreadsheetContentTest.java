package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Multiplication;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.local.GetSpreadsheetContent;

public class GetSpreadsheetContentTest extends BubbleDocsServiceTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "thepassword";
    private static final String EMAIL = "email@domain.org";
    private static final String NAME = "The Awesome Name";

    private static final String SS_NAME = "SS Name";
    private static final int ROWS = 3;
    private static final int COLUMNS = 3;

    private static final String OTHER_USERNAME = "other";
    private static final String OTHER_PASSWORD = "password";
    private static final String OTHER_EMAIL = "freds@domain.com";
    private static final String OTHER_NAME = "The Other";

    private static final String NOT_IN_SESSEION = "notinsession";

    private static String TOKEN;
    private static String OTHER_TOKEN;
    private static Integer ID;

    @Override
    public void populate4Test() {
        User user = createUser(USERNAME, PASSWORD, EMAIL, NAME);
        TOKEN = addUserToSession(USERNAME);

        createUser(OTHER_USERNAME, OTHER_PASSWORD, OTHER_EMAIL, OTHER_NAME);
        OTHER_TOKEN = addUserToSession(OTHER_USERNAME);

        Spreadsheet spreadsheet = createSpreadSheet(user, SS_NAME, ROWS, COLUMNS);
        ID = spreadsheet.getId();

        Cell c1 = new Cell(spreadsheet, 1, 1);
        c1.setContent(new Literal(5));

        Cell c2 = new Cell(spreadsheet, 1, 2);
        c2.setContent(new Reference(c1));

        Cell c3 = new Cell(spreadsheet, 2, 3);
        c3.setContent(new Literal(2));

        Cell c4 = new Cell(spreadsheet, 1, 3);
        c4.setContent(new Addition(new Literal(1), new Reference(c3)));

        Cell c5 = new Cell(spreadsheet, 2, 1);
        c5.setContent(new Multiplication(new Literal(3), new Literal(3)));
    }

    @Test
    public void success() {
        GetSpreadsheetContent service = new GetSpreadsheetContent(TOKEN, ID);
        service.execute();

        String[][] matrix = service.getResult();

        assertEquals("5", matrix[0][0]);
        assertEquals("5", matrix[0][1]);
        assertEquals("3", matrix[0][2]);
        assertEquals("9", matrix[1][0]);
        assertEquals("", matrix[1][1]);
        assertEquals("2", matrix[1][2]);
        assertEquals("", matrix[2][0]);
        assertEquals("", matrix[2][1]);
        assertEquals("", matrix[2][2]);
    }

    @Test
    public void successWithDivisionByZero() {
        Spreadsheet spreadsheet = getBubbleDocs().getSpreadsheetById(ID);
        new Cell(spreadsheet, 3, 1).setContent(new Division(new Literal(2), new Literal(0)));

        GetSpreadsheetContent service = new GetSpreadsheetContent(TOKEN, ID);
        service.execute();

        String[][] matrix = service.getResult();

        assertEquals("5", matrix[0][0]);
        assertEquals("5", matrix[0][1]);
        assertEquals("3", matrix[0][2]);
        assertEquals("9", matrix[1][0]);
        assertEquals("", matrix[1][1]);
        assertEquals("2", matrix[1][2]);
        assertEquals("#VALUE", matrix[2][0]);
        assertEquals("", matrix[2][1]);
        assertEquals("", matrix[2][2]);
    }

    @Test
    public void successWithReferenceToEmptyCell() {
        Spreadsheet spreadsheet = getBubbleDocs().getSpreadsheetById(ID);
        Cell referencedCell = new Cell(spreadsheet, 3, 3);
        new Cell(spreadsheet, 3, 1).setContent(new Reference(referencedCell));

        GetSpreadsheetContent service = new GetSpreadsheetContent(TOKEN, ID);
        service.execute();

        String[][] matrix = service.getResult();

        assertEquals("5", matrix[0][0]);
        assertEquals("5", matrix[0][1]);
        assertEquals("3", matrix[0][2]);
        assertEquals("9", matrix[1][0]);
        assertEquals("", matrix[1][1]);
        assertEquals("2", matrix[1][2]);
        assertEquals("#VALUE", matrix[2][0]);
        assertEquals("", matrix[2][1]);
        assertEquals("", matrix[2][2]);
    }

    @Test
    public void successWithFunctionWithReferenceToEmptyCell() {
        Spreadsheet spreadsheet = getBubbleDocs().getSpreadsheetById(ID);
        Cell referencedCell = new Cell(spreadsheet, 3, 3);
        new Cell(spreadsheet, 3, 2).setContent(new Addition(new Reference(referencedCell), new Literal(3)));

        GetSpreadsheetContent service = new GetSpreadsheetContent(TOKEN, ID);
        service.execute();

        String[][] matrix = service.getResult();

        assertEquals("5", matrix[0][0]);
        assertEquals("5", matrix[0][1]);
        assertEquals("3", matrix[0][2]);
        assertEquals("9", matrix[1][0]);
        assertEquals("", matrix[1][1]);
        assertEquals("2", matrix[1][2]);
        assertEquals("", matrix[2][0]);
        assertEquals("#VALUE", matrix[2][1]);
        assertEquals("", matrix[2][2]);
    }

    @Test(expected = UnauthorizedUserException.class)
    public void unauthorizedUser() {
        new GetSpreadsheetContent(OTHER_TOKEN, ID).execute();
    }

    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new GetSpreadsheetContent(NOT_IN_SESSEION, ID).execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new GetSpreadsheetContent(null, ID).execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new GetSpreadsheetContent("", ID).execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void nullSpreadsheetId() {
        new GetSpreadsheetContent(TOKEN, null).execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(TOKEN);
        new GetSpreadsheetContent(TOKEN, ID).execute();
    }

}
