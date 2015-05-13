package pt.tecnico.bubbledocs.integration.system;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

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

public class RemoteSystemIT {

    private final String USERNAME = "userTest";
    private final String EMAIL = "userTest@test.com";
    private final String NAME = "userTestName";
    private final String SPREADSHEET = "SpreadsheetTest";
    private final int NUM_ROWS = 4;
    private final int NUM_COLS = 4;

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
    public void testRemoteSystemIT() {
        LoginUserIntegrator loginRoot = new LoginUserIntegrator("root", "root");
        loginRoot.execute();
        String userToken = loginRoot.getUserToken();
        new CreateUserIntegrator(userToken, USERNAME, EMAIL, NAME).execute();
        CreateSpreadsheetIntegrator spreadsheet = new CreateSpreadsheetIntegrator(userToken, SPREADSHEET, NUM_ROWS, NUM_COLS);
        spreadsheet.execute();
        int sheetId = spreadsheet.getSheetId();
        new AssignLiteralCellIntegrator(userToken, sheetId, "2;2", "123").execute();
        new AssignLiteralCellIntegrator(userToken, sheetId, "2;3", "34").execute();
        new AssignReferenceCellIntegrator(userToken, sheetId, "1;3", "2;3").execute();
        new AssignBinaryFunctionToCellIntegrator(userToken, sheetId, "1;2", "ADD(5,5)").execute();
        new AssignBinaryFunctionToCellIntegrator(userToken, sheetId, "1;1", "ADD(1;2,1;3)").execute();
        new GetSpreadsheetContentIntegrator(userToken, sheetId).execute();
        new ExportDocumentIntegrator(userToken, sheetId).execute();
        new ImportDocumentIntegrator(SPREADSHEET, userToken).execute();
        new RenewPasswordIntegrator(userToken);
        new DeleteUserIntegrator(userToken, USERNAME).execute();

    }

}
