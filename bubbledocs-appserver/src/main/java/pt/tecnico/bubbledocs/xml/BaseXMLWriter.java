package pt.tecnico.bubbledocs.xml;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Multiplication;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.Subtraction;
import pt.tecnico.bubbledocs.domain.Literal;

public class BaseXMLWriter implements XMLWriter {

	@Override
	public Element visit(Spreadsheet ss) {
		Element spreadsheetElement = new Element("Spreadsheet");
		
		spreadsheetElement.setAttribute("rows", ss.getRows().toString());
		spreadsheetElement.setAttribute("columns", ss.getColumns().toString());
		spreadsheetElement.setAttribute("id", ss.getId().toString());
		spreadsheetElement.setAttribute("name", ss.getName());
		spreadsheetElement.setAttribute("created", ss.getCreationDate().toString());
		
		Element cellsElement = new Element("Cells");
		spreadsheetElement.addContent(cellsElement);
		
		for (Cell cell: ss.getCellsSet()) {
			cellsElement.addContent(cell.accept(this));
		}
		
		// TODO: add perms
		//Element permissionsElement = new Element("Permissions");
		//spreadsheetElement.addContent(permissionsElement);
		
		return spreadsheetElement;
	}

	@Override
	public Element visit(Cell cell) {
		Element cellElement = new Element("Cell");
		
		cellElement.setAttribute("row", cell.getRow().toString());
		cellElement.setAttribute("column", cell.getColumn().toString());
		
		cellElement.addContent(cell.getContent().accept(this));
		
		return cellElement;
	}

	@Override
	public Element visit(Addition add) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element visit(Subtraction sub) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element visit(Multiplication mul) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element visit(Division div) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element visit(Literal literal) {
		Element literalElement = new Element("Literal");
		literalElement.addContent(literal.getLiteral().toString());
		return literalElement;
	}

	@Override
	public Element visit(Reference reference) {
		Element referenceElement = new Element("Reference");
		Element cellElement = new Element("Cell");
		cellElement.setAttribute("row", reference.getCell().getRow().toString());
		cellElement.setAttribute("column", reference.getCell().getColumn().toString());
		referenceElement.addContent(cellElement);
		return referenceElement;
	}

}
