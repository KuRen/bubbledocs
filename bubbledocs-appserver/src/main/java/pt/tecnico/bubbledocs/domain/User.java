package pt.tecnico.bubbledocs.domain;

import java.util.ArrayList;
import java.util.List;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;

public class User extends User_Base {

    protected User() {
        super();
    }

    public User(String username, String password, String name) {
        super();
        BubbleDocs bd = BubbleDocs.getInstance();
        setUsername(username);
        setPassword(password);
        setName(name);
        setBubbledocs(bd);
    }

    public void init(String username, String password, String name) {
        setUsername(username);
        setPassword(password);
        setName(name);
    }

    @Atomic(mode = TxMode.READ)
    public static List<Spreadsheet> getSpreadsheetsByName(User owner, String name) {
        List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();
        for (Spreadsheet s : owner.getSpreadsheetsSet())
            if (s.getName().equals(name))
                spreadsheets.add(s);
        return spreadsheets;
    }

    public List<Spreadsheet> getSpreadsheetsByName(String name) {
        return getSpreadsheetsByName(this, name);
    }

    public void delete() {
        setBubbledocs(null);
        if (getSession() != null)
            getSession().delete();
        setSession(null);
        for (Permission permission : getPermissionsSet()) {
            removePermissions(permission);
        }
        for (Spreadsheet spreadsheet : getSpreadsheetsSet()) {
            removeSpreadsheets(spreadsheet);
            spreadsheet.delete();
        }
        deleteDomainObject();
    }

    public boolean isRoot() {
        return false;
    }
}
