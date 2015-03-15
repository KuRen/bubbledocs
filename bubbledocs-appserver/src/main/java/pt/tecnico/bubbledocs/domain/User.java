package pt.tecnico.bubbledocs.domain;

import java.util.ArrayList;

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
    }

    public void init(String username, String password, String name) {
        setUsername(username);
        setPassword(password);
        setName(name);
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
