package pt.tecnico.bubbledocs.integration.system;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.bubbledocs.integration.AssignBinaryFunctionToCellIntegrator;
import pt.tecnico.bubbledocs.integration.AssignLiteralCellIntegrator;
import pt.tecnico.bubbledocs.integration.AssignReferenceCellIntegrator;
import pt.tecnico.bubbledocs.integration.CreateSpreadsheetIntegrator;
import pt.tecnico.bubbledocs.integration.CreateUserIntegrator;
import pt.tecnico.bubbledocs.integration.DeleteUserIntegrator;
import pt.tecnico.bubbledocs.integration.ExportDocumentIntegrator;
import pt.tecnico.bubbledocs.integration.GetSpreadsheetContentIntegrator;
import pt.tecnico.bubbledocs.integration.ImportDocumentIntegrator;
import pt.tecnico.bubbledocs.integration.LoginUserIntegrator;
import pt.tecnico.bubbledocs.integration.RenewPasswordIntegrator;
import pt.tecnico.bubbledocs.service.dto.AuthenticationResult;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class LocalSystemTest {

    private final String USERNAME = "userTest";
    private final String EMAIL = "userTest@test.com";
    private final String NAME = "userTestName";
    private final String SPREADSHEET = "SpreadsheetTest";
    private final int NUM_ROWS = 4;
    private final int NUM_COLS = 4;

    @Mocked
    IDRemoteServices idRemoteServices;

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testLocalSystem() {
        new MockUp<StoreRemoteServices>() {
            @Mock
            public void storeDocument(String username, String docName, byte[] document) {
            }

            @Mock
            public byte[] loadDocument(String username, String docName) {
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<Spreadsheet rows=\"4\" columns=\"4\" id=\"1\" name=\"SpreadsheetTest\" owner=\"root\" created=\"2015-05-01T05:37:18.019+01:00\"><Cells><Cell row=\"1\" column=\"1\"><Addition><Reference><Cell row=\"1\" column=\"2\" /></Reference><Reference><Cell row=\"1\" column=\"3\" /></Reference></Addition></Cell><Cell row=\"1\" column=\"2\"><Addition><Literal literal=\"5\" /><Literal literal=\"5\" /></Addition></Cell><Cell row=\"1\" column=\"3\"><Reference><Cell row=\"2\" column=\"3\" /></Reference></Cell><Cell row=\"2\" column=\"3\"><Literal literal=\"34\" /></Cell><Cell row=\"2\" column=\"2\"><Literal literal=\"123\" /></Cell></Cells></Spreadsheet>"
                        .getBytes();
            }
        };

        new MockUp<IDRemoteServices>() {
            @Mock
            public AuthenticationResult loginUser(String username, String password) {
                return new AuthenticationResult("key_" + username, "ticket_" + username);
            }

            @Mock
            public void createUser(String username, String email) {

            }

            @Mock
            public void renewPassword(String token) {

            }

            @Mock
            public void removeUser(String username) {

            }
        };

        LoginUserIntegrator loginRoot = new LoginUserIntegrator("root", "root");
        loginRoot.execute();
        String rootToken = loginRoot.getUserToken();

        new CreateUserIntegrator(rootToken, USERNAME, EMAIL, NAME).execute();

        //LoginUserIntegrator loginUser = new LoginUserIntegrator(USERNAME, null); //Talvez nao de para testar isto
        //loginUser.execute();

        CreateSpreadsheetIntegrator spreadsheet = new CreateSpreadsheetIntegrator(rootToken, SPREADSHEET, NUM_ROWS, NUM_COLS);
        spreadsheet.execute();
        int sheetId = spreadsheet.getSheetId();

        new AssignLiteralCellIntegrator(rootToken, sheetId, "2;2", "123").execute();
        new AssignLiteralCellIntegrator(rootToken, sheetId, "2;3", "34").execute();
        new AssignReferenceCellIntegrator(rootToken, sheetId, "1;3", "2;3").execute();
        new AssignBinaryFunctionToCellIntegrator(rootToken, sheetId, "1;2", "ADD(5,5)").execute();
        new AssignBinaryFunctionToCellIntegrator(rootToken, sheetId, "1;1", "ADD(1;2,1;3)").execute();

        new GetSpreadsheetContentIntegrator(rootToken, sheetId).execute();

        new ExportDocumentIntegrator(rootToken, sheetId).execute();
        new ImportDocumentIntegrator(SPREADSHEET, rootToken).execute();

        new RenewPasswordIntegrator(rootToken).execute();

        new DeleteUserIntegrator(rootToken, USERNAME).execute();
    }

}
