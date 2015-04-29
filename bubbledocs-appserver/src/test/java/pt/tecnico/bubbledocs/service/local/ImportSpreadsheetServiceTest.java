package pt.tecnico.bubbledocs.service.local;

import org.junit.Assert;
import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class ImportSpreadsheetServiceTest extends BubbleDocsServiceTest {

    private final String NAME = "namelastname";
    private final String EMAIL = "namelastname@example.com";
    private final String USERNAME = "lastname";
    private final String PASSWORD = "password";
    private final String SS_NAME = "A SS Name";
    private final String NOT_IN_SESSION_TOKEN = "notinsession";
    
    private String authorizedToken;
    private String unauthorizedToken;
    private byte[] document = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Spreadsheet rows=\"1\" columns=\"1\" id=\"99\" name=\"A SS Name\" owner=\"lastname\" created=\"2015-04-24T20:25:06.747+01:00\"><Cells><Cell row=\"1\" column=\"1\"><Literal literal=\"2\" /></Cell></Cells></Spreadsheet>".getBytes();
    
    //token nulo
    @Test(expected = InvalidArgumentException.class)
    public void nullToken() {
        new ImportSpreadsheetService(document, null).execute();
    }

    //Token invalido
    @Test(expected = InvalidArgumentException.class)
    public void emptyToken() {
        new ImportSpreadsheetService(document, "").execute();
    }

    //user nao esta em sessao
    @Test(expected = UserNotInSessionException.class)
    public void userNotInSession() {
        new ImportSpreadsheetService(document, NOT_IN_SESSION_TOKEN).execute();
    }

    //token expirado
    @Test(expected = TokenExpiredException.class)
    public void expiredToken() {
        expireToken(authorizedToken);
        new ImportSpreadsheetService(document, authorizedToken).execute();
    }
    
    //username nao autorizado
    @Test(expected = UnauthorizedUserException.class)
    public void unauthorizedUser() {
        expireToken(authorizedToken);
        new ImportSpreadsheetService(document, unauthorizedToken).execute();
    }

    @Test
    public void testImportSpreadsheetService() {
        ImportSpreadsheetService service = new ImportSpreadsheetService(document, authorizedToken);
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
        createUser("user2", "user2", "user2@hotmail.com", "user2");
        unauthorizedToken = addUserToSession("user2");
    }
}
