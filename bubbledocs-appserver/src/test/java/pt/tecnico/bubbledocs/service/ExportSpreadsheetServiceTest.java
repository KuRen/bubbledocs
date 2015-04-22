package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.junit.Ignore;
import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.ImportDocumentException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NonExistingSpreadsheetException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.local.ExportSpreadsheetService;

public class ExportSpreadsheetServiceTest extends BubbleDocsServiceTest {

    private String authorizedToken;
    private String unauthorizedToken;
    private String writeToken;
    private String readToken;
    private int validId;

    private static final int VALUE = 2;
    private static final int ROWS = 30;
    private static final int COLS = 40;
    private static final int CELL_1_COL = 1;
    private static final int CELL_1_ROW = 1;
    private static final int CELL_2_COL = 2;
    private static final int CELL_2_ROW = 2;

    private static final int INVALID_ID = -42;
    private static final int NOT_EXISTENT_ID = 42000;
    private static final String EMPTY_TOKEN = "";
    private static final String NOT_IN_SESSION_TOKEN = "not in session!!!";
    private static final String USER = "userName";
    private static final String SS_NAME = "A SS Name";
    private static final String WRITER_USER = "wUser";
    private static final String READER_USER = "rUser";

    @Test(expected = InvalidSpreadSheetIdException.class)
    public void invalidSpreadsheet() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(INVALID_ID, authorizedToken);
        service.execute();
    }

    @Test(expected = NonExistingSpreadsheetException.class)
    public void nonExistingSpreadsheet() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(NOT_EXISTENT_ID, authorizedToken);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, EMPTY_TOKEN);
        service.execute();
    }

    @Test
    @Ignore("The token format isn't known yet")
    public void invalidToken() {

    }

    @Test(expected = UserNotInSessionException.class)
    public void UserNotInSession() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, NOT_IN_SESSION_TOKEN);
        service.execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(authorizedToken);
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, authorizedToken);
        service.execute();
    }

    @Test(expected = UnauthorizedUserException.class)
    public void unauthUser() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, unauthorizedToken);
        service.execute();
    }

    @Test
    //This is not extensive, the extensive tests should be done in the units who can convert themselves to XML
    public void success() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, authorizedToken);
        service.execute();

        byte[] result = service.getResult();

        Document doc;

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        try {
            doc = builder.build(new ByteArrayInputStream(result));
        } catch (JDOMException | IOException e) {
            throw new ImportDocumentException();
        }

        Element rootElement = doc.getRootElement();

        assertEquals(USER, rootElement.getAttribute("owner").getValue());
        assertEquals(ROWS, Integer.parseInt(rootElement.getAttribute("rows").getValue()));
        assertEquals(COLS, Integer.parseInt(rootElement.getAttribute("columns").getValue()));
        assertEquals(SS_NAME, rootElement.getAttribute("name").getValue());

        Element cells = rootElement.getChild("Cells");

        assertEquals(cells.getChildren("Cell").size(), 2);

        for (Element cellElement : cells.getChildren("Cell")) {
            Integer row = Integer.parseInt(cellElement.getAttribute("row").getValue());
            Integer column = Integer.parseInt(cellElement.getAttribute("column").getValue());

            if (row == CELL_1_ROW && column == CELL_1_COL) {
                assertEquals("Literal", cellElement.getChildren().get(0).getName());
                assertEquals(VALUE, Integer.parseInt(cellElement.getChildren().get(0).getAttribute("literal").getValue()));
            }

            else if (row == CELL_2_ROW && column == CELL_2_COL) {
                assertEquals("Addition", cellElement.getChildren().get(0).getName());

                Element arg1Element = cellElement.getChildren().get(0).getChildren().get(0);
                Element arg2Element = cellElement.getChildren().get(0).getChildren().get(1);

                assertEquals("Reference", arg1Element.getName());
                assertEquals("Literal", arg2Element.getName());

                Element arg1CellElement = arg1Element.getChildren().get(0);

                assertEquals(CELL_1_ROW, Integer.parseInt(arg1CellElement.getAttribute("row").getValue()));
                assertEquals(CELL_1_COL, Integer.parseInt(arg1CellElement.getAttribute("column").getValue()));

                assertEquals("Literal", arg2Element.getName());
                assertEquals(VALUE, Integer.parseInt(arg2Element.getAttribute("literal").getValue()));
            }

            else {
                fail("Non existing cells created during export");
            }
        }
    }

    @Test
    //This is not extensive, the extensive tests should be done in the units who can convert themselves to XML
    public void readOnlysuccess() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, readToken);
        service.execute();

        byte[] result = service.getResult();

        Document doc;

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        try {
            doc = builder.build(new ByteArrayInputStream(result));
        } catch (JDOMException | IOException e) {
            throw new ImportDocumentException();
        }

        Element rootElement = doc.getRootElement();

        assertEquals(USER, rootElement.getAttribute("owner").getValue());
        assertEquals(ROWS, Integer.parseInt(rootElement.getAttribute("rows").getValue()));
        assertEquals(COLS, Integer.parseInt(rootElement.getAttribute("columns").getValue()));
        assertEquals(SS_NAME, rootElement.getAttribute("name").getValue());

        Element cells = rootElement.getChild("Cells");

        assertEquals(cells.getChildren("Cell").size(), 2);

        for (Element cellElement : cells.getChildren("Cell")) {
            Integer row = Integer.parseInt(cellElement.getAttribute("row").getValue());
            Integer column = Integer.parseInt(cellElement.getAttribute("column").getValue());

            if (row == CELL_1_ROW && column == CELL_1_COL) {
                assertEquals("Literal", cellElement.getChildren().get(0).getName());
                assertEquals(VALUE, Integer.parseInt(cellElement.getChildren().get(0).getAttribute("literal").getValue()));
            }

            else if (row == CELL_2_ROW && column == CELL_2_COL) {
                assertEquals("Addition", cellElement.getChildren().get(0).getName());

                Element arg1Element = cellElement.getChildren().get(0).getChildren().get(0);
                Element arg2Element = cellElement.getChildren().get(0).getChildren().get(1);

                assertEquals("Reference", arg1Element.getName());
                assertEquals("Literal", arg2Element.getName());

                Element arg1CellElement = arg1Element.getChildren().get(0);

                assertEquals(CELL_1_ROW, Integer.parseInt(arg1CellElement.getAttribute("row").getValue()));
                assertEquals(CELL_1_COL, Integer.parseInt(arg1CellElement.getAttribute("column").getValue()));

                assertEquals("Literal", arg2Element.getName());
                assertEquals(VALUE, Integer.parseInt(arg2Element.getAttribute("literal").getValue()));
            }

            else {
                fail("Non existing cells created during export");
            }
        }
    }

    @Test
    //This is not extensive, the extensive tests should be done in the units who can convert themselves to XML
    public void writeSuccess() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(validId, writeToken);
        service.execute();

        byte[] result = service.getResult();

        Document doc;

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        try {
            doc = builder.build(new ByteArrayInputStream(result));
        } catch (JDOMException | IOException e) {
            throw new ImportDocumentException();
        }

        Element rootElement = doc.getRootElement();

        assertEquals(USER, rootElement.getAttribute("owner").getValue());
        assertEquals(ROWS, Integer.parseInt(rootElement.getAttribute("rows").getValue()));
        assertEquals(COLS, Integer.parseInt(rootElement.getAttribute("columns").getValue()));
        assertEquals(SS_NAME, rootElement.getAttribute("name").getValue());

        Element cells = rootElement.getChild("Cells");

        assertEquals(cells.getChildren("Cell").size(), 2);

        for (Element cellElement : cells.getChildren("Cell")) {
            Integer row = Integer.parseInt(cellElement.getAttribute("row").getValue());
            Integer column = Integer.parseInt(cellElement.getAttribute("column").getValue());

            if (row == CELL_1_ROW && column == CELL_1_COL) {
                assertEquals("Literal", cellElement.getChildren().get(0).getName());
                assertEquals(VALUE, Integer.parseInt(cellElement.getChildren().get(0).getAttribute("literal").getValue()));
            }

            else if (row == CELL_2_ROW && column == CELL_2_COL) {
                assertEquals("Addition", cellElement.getChildren().get(0).getName());

                Element arg1Element = cellElement.getChildren().get(0).getChildren().get(0);
                Element arg2Element = cellElement.getChildren().get(0).getChildren().get(1);

                assertEquals("Reference", arg1Element.getName());
                assertEquals("Literal", arg2Element.getName());

                Element arg1CellElement = arg1Element.getChildren().get(0);

                assertEquals(CELL_1_ROW, Integer.parseInt(arg1CellElement.getAttribute("row").getValue()));
                assertEquals(CELL_1_COL, Integer.parseInt(arg1CellElement.getAttribute("column").getValue()));

                assertEquals("Literal", arg2Element.getName());
                assertEquals(VALUE, Integer.parseInt(arg2Element.getAttribute("literal").getValue()));
            }

            else {
                fail("Non existing cells created during export");
            }
        }
    }

    @Override
    public void populate4Test() {
        createUser(USER, "password", "ola@gmail.com", "big complete name");
        authorizedToken = addUserToSession(USER);

        createUser("noobUser", "password", "ole@hotmail.com", "yet a bigger name");
        unauthorizedToken = addUserToSession("noobUser");

        createUser(WRITER_USER, "password", "oli@megamail.pt", "a bigger bigger name");
        writeToken = addUserToSession(WRITER_USER);

        createUser(READER_USER, "password", "olo@yahoo.fr", "yet a bigger bigger name");
        readToken = addUserToSession(READER_USER);

        BubbleDocs bd = BubbleDocs.getInstance();

        Spreadsheet ss = createSpreadSheet(bd.getUserByUsername(USER), SS_NAME, ROWS, COLS);
        validId = ss.getId();
        Cell c1 = new Cell(ss, CELL_1_ROW, CELL_1_COL);
        Cell c2 = new Cell(ss, CELL_2_ROW, CELL_2_COL);
        c1.setContent(new Literal(VALUE));
        c2.setContent(new Addition(new Reference(c1), new Literal(VALUE)));

        ss.addCells(c1);
        ss.addCells(c2);

        Permission writePermission = new Permission();
        writePermission.setPermission(PermissionType.WRITE);
        writePermission.setUser(bd.getUserByUsername(WRITER_USER));
        ss.addPermissions(writePermission, bd.getUserByUsername(USER));

        Permission readPermission = new Permission();
        readPermission.setPermission(PermissionType.READ);
        readPermission.setUser(bd.getUserByUsername(READER_USER));
        ss.addPermissions(readPermission, bd.getUserByUsername(USER));

    }
}
