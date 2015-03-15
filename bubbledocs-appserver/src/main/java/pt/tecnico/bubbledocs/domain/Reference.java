package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Reference extends Reference_Base implements XMLable {
    
    protected Reference() {
        super();
    }
            
    public Reference(Cell cell) {
        super();
        setReferencedCell(cell);
    }
    
    @Override
	public Element accept(XMLWriter writer) {
		return writer.visit(this);
	}

	@Override
	public void importFromXML(Element contentElement) {
		Cell cell = new Cell();
		Element cellElement = contentElement.getChildren().get(0);
		cell.setRow(Integer.parseInt(cellElement.getAttribute("row").getValue()));
		cell.setColumn(Integer.parseInt(cellElement.getAttribute("column").getValue()));
		setCell(cell);
	}

	@Override
	public Integer getValue() {
		return getCell().getContent().getValue();
	}
	
	@Override
	public void delete() {
	    setReferencedCell(null);
        setCell(null);
        deleteDomainObject();
	}
    
}
