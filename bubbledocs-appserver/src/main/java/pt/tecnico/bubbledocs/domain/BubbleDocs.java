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
        addUsers(Root.getInstance());
        setManager(new SessionManager());
    }

    public Spreadsheet getSpreadsheetById(Integer id) {
        for (Spreadsheet spreadsheet : getSpreadsheetsSet())
            if (spreadsheet.getId().equals(id))
                return spreadsheet;
        return null;
    }

    public void importSpreadsheetFromXML(Element spreadsheetElement) {
        Spreadsheet spreadsheet = new Spreadsheet();

        spreadsheet.importFromXML(spreadsheetElement);

        addSpreadsheets(spreadsheet);
    }

    @Atomic(mode = TxMode.READ)
    public User getUserByUsername(String username) {
        for (User user : getUsersSet())
            if (user.getUsername().equals(username))
                return user;
        return null;
    }

    public User findUserByToken(String token) {
        return getManager().findUserByToken(token);
    }

    String addUserToSession(String username) {
        return addUserToSession(getUserByUsername(username));
    }

    String addUserToSession(User user) {
        return getManager().addUserToSession(user);
    }

}
