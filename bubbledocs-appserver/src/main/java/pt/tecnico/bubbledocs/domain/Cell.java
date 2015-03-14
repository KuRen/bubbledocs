package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Cell extends Cell_Base implements XMLable {

    public Cell(Integer row, Integer column) {
        super();
        setRow(row);
        setColumn(column);
        setContent(null);
    }

    @Override
    public Element accept(XMLWriter writer) {
        return writer.visit(this);
    }

    public void delete() {
        getContent().delete();
        this.setSpreadsheet(null);
        deleteDomainObject();
    }
}
