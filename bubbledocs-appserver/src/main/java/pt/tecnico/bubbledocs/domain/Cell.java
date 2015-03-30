package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.exception.EmptyValueException;
import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Cell extends Cell_Base implements XMLable {

    protected Cell() {
        super();
    }

    public Cell(Spreadsheet ss, Integer row, Integer column) {
        super();
        setRow(row);
        setColumn(column);
        setContent(null);
        setSpreadsheet(ss);
    }

    public void importFromXML(Element cellElement) {
        setRow(Integer.parseInt(cellElement.getAttribute("row").getValue()));
        setColumn(Integer.parseInt(cellElement.getAttribute("column").getValue()));

        Element contentElement = cellElement.getChildren().get(0);

        String elementType = contentElement.getName();

        Content content = Content.contentFactory(elementType);
        setContent(content);
        content.importFromXML(contentElement);

    }

    public String asString() {
        if (getContent() == null)
            throw new EmptyValueException();
        return getContent().asString();
    }

    @Override
    public Element accept(XMLWriter writer) {
        return writer.visit(this);
    }

    public void delete() {

        if (getReferenceSet().size() != 0) {
            for (Reference r : getReferenceSet())
                r.setReferencedCell(null);
        }

        if (getContent() != null) {
            getContent().delete();
        }

        setSpreadsheet(null);

        deleteDomainObject();

    }
}
