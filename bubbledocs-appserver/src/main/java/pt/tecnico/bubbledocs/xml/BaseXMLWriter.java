package pt.tecnico.bubbledocs.xml;

import org.jdom2.Element;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BinaryFunction;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Multiplication;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.Subtraction;

public class BaseXMLWriter implements XMLWriter {

    @Override
    public Element visit(Spreadsheet ss) {
        Element spreadsheetElement = new Element("Spreadsheet");

        spreadsheetElement.setAttribute("rows", ss.getRows().toString());
        spreadsheetElement.setAttribute("columns", ss.getColumns().toString());
        spreadsheetElement.setAttribute("id", ss.getId().toString());
        spreadsheetElement.setAttribute("name", ss.getName());
        spreadsheetElement.setAttribute("owner", ss.getOwner().getUsername());
        spreadsheetElement.setAttribute("created", ss.getCreationDate().toString());

        Element cellsElement = new Element("Cells");
        spreadsheetElement.addContent(cellsElement);

        for (Cell cell : ss.getCellsSet()) {
            cellsElement.addContent(cell.accept(this));
        }

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
        Element addElement = new Element("Addition");
        addBinaryFunctionArguments(add, addElement);
        return addElement;
    }

    @Override
    public Element visit(Subtraction sub) {
        Element subElement = new Element("Subtraction");
        addBinaryFunctionArguments(sub, subElement);
        return subElement;
    }

    @Override
    public Element visit(Multiplication mul) {
        Element mulElement = new Element("Multiplication");
        addBinaryFunctionArguments(mul, mulElement);
        return mulElement;
    }

    @Override
    public Element visit(Division div) {
        Element divElement = new Element("Division");
        addBinaryFunctionArguments(div, divElement);
        return divElement;
    }

    @Override
    public Element visit(Literal literal) {
        Element literalElement = new Element("Literal");
        literalElement.setAttribute("literal", literal.getLiteral().toString());
        return literalElement;
    }

    @Override
    public Element visit(Reference reference) {
        Element referenceElement = new Element("Reference");
        Element cellElement = new Element("Cell");
        cellElement.setAttribute("row", reference.getReferencedCell().getRow().toString());
        cellElement.setAttribute("column", reference.getReferencedCell().getColumn().toString());
        referenceElement.addContent(cellElement);
        return referenceElement;
    }

    private void addBinaryFunctionArguments(BinaryFunction function, Element element) {
        element.addContent(function.getArgument1().accept(this));
        element.addContent(function.getArgument2().accept(this));
    }

}
