package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Cell extends Cell_Base implements XMLable {

	protected Cell() {
		super();
	}
	
    public Cell(Integer row, Integer column) {
        super();
        setRow(row);
        setColumn(column);
        setContent(null);
    }
    
    public void importFromXML(Element cellElement) {
    	setRow(Integer.parseInt(cellElement.getAttribute("row").getValue()));
    	setColumn(Integer.parseInt(cellElement.getAttribute("column").getValue()));
    	
    	Element contentElement = cellElement.getChildren().get(0);
    	
    	String elementType = contentElement.getName();
    	
    	setContent(Content.importFromXML(contentElement, elementType));
    	
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
