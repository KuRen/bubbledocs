package pt.tecnico.bubbledocs.domain;

public class Spreadsheet extends Spreadsheet_Base {
    
    public Spreadsheet(Integer rows, Integer columns, String name, User user) {
        super();
        setRows(rows);
        setColumns(columns);
        setName(name);
        setOwner(user);
    }
    
}
