package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Spreadsheet extends Spreadsheet_Base implements XMLable {
	
	protected Spreadsheet() {
		super();
	}

    public Spreadsheet(Integer rows, Integer columns, String name, User user) {
        super();
        setRows(rows);
        setColumns(columns);
        setName(name);
        setOwner(user);
    }
    
    public void importFromXML(Element spreadsheetElement) {
    	
    	Integer rows = new Integer(spreadsheetElement.getAttribute("rows").getValue());
    	setRows(rows);
        Integer columns = new Integer(spreadsheetElement.getAttribute("columns").getValue());
        setColumns(columns);
        String name = spreadsheetElement.getAttribute("name").getValue();
        setName(name);
        DateTime date = new DateTime(); //FIXME
        //date = parse(spreadsheetElement.getAttribute("date").getValue());
        //User user = getUserByUsername(spreadsheetElement.getAttribute("user").getValue()); //FIXME?
        
    	// clear current Spreadsheet
    	for (Cell cell : getCellsSet())
    		cell.delete();
    	
    	Element cells = spreadsheetElement.getChild("cells");
    	for (Element cellElement : cells.getChildren("cell")) {
    		Cell cell = new Cell(null, null);
    		cell.importFromXML(cellElement);
    		addCells(cell);
    	}
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
