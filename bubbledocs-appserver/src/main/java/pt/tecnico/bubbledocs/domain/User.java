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
        setUsername(username);
        setPassword(password);
        setName(name);

        BubbleDocs bd = BubbleDocs.getInstance();
        setBubbledocs(bd);
    }

    public void init(String username, String password, String name) {
        setUsername(username);
        setPassword(password);
        setName(name);
    }

    public static List<Spreadsheet> getSpreadsheetsByUser(User owner, String name) {
        List<Spreadsheet> spreadsheets = new ArrayList<Spreadsheet>();
        for (Spreadsheet s : owner.getSpreadsheetsSet())
            if (s.getName().equals(name))
                spreadsheets.add(s);
        return spreadsheets;
    }

    @Atomic(mode = TxMode.READ)
    public ArrayList<Spreadsheet> getOwnSpreadsheetsByName(String name) {
        ArrayList<Spreadsheet> list = new ArrayList<Spreadsheet>();

        for (Spreadsheet s : getSpreadsheetsSet())
            if (s.getName().equals(name))
                list.add(s);

        return list;
    }
}
