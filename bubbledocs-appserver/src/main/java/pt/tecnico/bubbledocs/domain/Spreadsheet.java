package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Spreadsheet extends Spreadsheet_Base implements XMLable {

    public Spreadsheet(Integer rows, Integer columns, String name, User user) {
        super();
        setRows(rows);
        setColumns(columns);
        setName(name);
        setOwner(user);
    }

    @Override
    public Element accept(XMLWriter writer) {
        return writer.visit(this);
    }

    public void delete() {

        for (Cell c : getCellsSet())
            c.delete();
        for (Permission p : this.getPermissionsSet())
            p.delete();

        this.setOwner(null);
        this.setBubbledocs(null);
        deleteDomainObject();
    }
}
