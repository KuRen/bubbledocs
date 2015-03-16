package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class BubbleDocs extends BubbleDocs_Base {

    public static BubbleDocs getInstance() {
        BubbleDocs bd = FenixFramework.getDomainRoot().getBubbleDocs();
        if (bd == null)
            bd = new BubbleDocs();

        return bd;
    }

    private BubbleDocs() {
        FenixFramework.getDomainRoot().setBubbleDocs(this);
        setSheetsID(1);
    }

    public Spreadsheet getSpreadsheetById(Integer id) {
        for (Spreadsheet spreadsheet : getSpreadsheetsSet())
            if (spreadsheet.getId().equals(id))
                return spreadsheet;
        return null;
    }

    public void importSpreadsheetFromXML(Element spreadsheetElement) {
        //TODO Verify user

        Spreadsheet spreadsheet = new Spreadsheet();

        spreadsheet.importFromXML(spreadsheetElement);

        addSpreadsheets(spreadsheet);
    }

    @Atomic(mode = TxMode.READ)
    public User getUserByUsername(String username) {

        for (User u : getUsersSet()) {
            if (u.getUsername().equals(username)) {
                return u;
            }
        }

        // If user not found
        return null;
    }

}
