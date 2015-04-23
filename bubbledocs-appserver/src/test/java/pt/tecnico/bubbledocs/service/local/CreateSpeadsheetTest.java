package pt.tecnico.bubbledocs.service.local;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;

public class CreateSpeadsheetTest extends BubbleDocsServiceTest {

    private String token;

    private static final int ZERO = 0;
    private static final int NEGATIVE = -2;
    private static final int POSITIVE = 5;
    private static final String EMPTY_NAME = "";
    private static final String ANY_NAME = "name";
    private static final String EMPTY_TOKEN = "";
    private static final String NOT_IN_SESSION_TOKEN = "not in session!!!";

    @Test(expected = InvalidArgumentException.class)
    public void zeroRows() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, ZERO, POSITIVE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void zeroColumns() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, POSITIVE, ZERO);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void negativeRows() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, NEGATIVE, POSITIVE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void negativeColumns() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, POSITIVE, NEGATIVE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptySpreadsheetName() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, EMPTY_NAME, POSITIVE, POSITIVE);
        service.execute();
    }

    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        CreateSpreadSheet service = new CreateSpreadSheet(EMPTY_TOKEN, ANY_NAME, POSITIVE, POSITIVE);
        service.execute();
    }

    @Test
    @Ignore("The token format isn't known yet")
    public void invalidToken() {

    }

    @Test(expected = UserNotInSessionException.class)
    public void UserNotInSession() {
        CreateSpreadSheet service = new CreateSpreadSheet(NOT_IN_SESSION_TOKEN, ANY_NAME, POSITIVE, POSITIVE);
        service.execute();
    }

    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(token);
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, POSITIVE, POSITIVE);
        service.execute();
    }

    @Test
    public void success() {
        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, POSITIVE, POSITIVE);
        service.execute();

        User user = getUserFromSession(token);
        Spreadsheet spreadsheet = FenixFramework.getDomainRoot().getBubbleDocs().getSpreadsheetById(service.getSheetId());

        assertEquals(spreadsheet.getRows().intValue(), POSITIVE);
        assertEquals(spreadsheet.getColumns().intValue(), POSITIVE);
        assertEquals(spreadsheet.getName(), ANY_NAME);
        assertEquals(spreadsheet.getOwner().getUsername(), user.getUsername());
    }

    @Test
    public void successWithExistingName() {
        User user = getUserFromSession(token);
        createSpreadSheet(user, ANY_NAME, POSITIVE, POSITIVE);

        CreateSpreadSheet service = new CreateSpreadSheet(token, ANY_NAME, POSITIVE, POSITIVE);
        service.execute();

        Spreadsheet spreadsheet = FenixFramework.getDomainRoot().getBubbleDocs().getSpreadsheetById(service.getSheetId());

        assertEquals(spreadsheet.getRows().intValue(), POSITIVE);
        assertEquals(spreadsheet.getColumns().intValue(), POSITIVE);
        assertEquals(spreadsheet.getName(), ANY_NAME);
        assertEquals(spreadsheet.getOwner().getUsername(), user.getUsername());
    }

    @Override
    public void populate4Test() {
        createUser("username", "password", "email", "name");
        this.token = addUserToSession("username");
    }
}
