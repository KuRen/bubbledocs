package pt.tecnico.bubbledocs.domain;

import java.util.Set;

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
        Element cellElement = contentElement.getChildren().get(0);
        Integer row = Integer.parseInt(cellElement.getAttribute("row").getValue());
        Integer column = Integer.parseInt(cellElement.getAttribute("column").getValue());

        Spreadsheet spreadsheet = null;

        if (getCell() == null) {
            // This Reference is inside a function, it has no cell
            Set<BinaryFunction> funcs = (getFunctionArg1Set().isEmpty()) ? getFunctionArg2Set() : getFunctionArg1Set();
            for (BinaryFunction bFunction : funcs) {
                spreadsheet = bFunction.getCell().getSpreadsheet();
                break;
            }
        } else {
            spreadsheet = getCell().getSpreadsheet();
        }

        Cell cell = spreadsheet.findCell(row, column);

        if (cell == null) {
            cell = new Cell();
            cell.setRow(row);
            cell.setColumn(column);
            spreadsheet.addCells(cell);
        }

        setReferencedCell(cell);
    }

    @Override
    public Integer getValue() {
        return getReferencedCell().getContent().getValue();
    }

    @Override
    public void delete() {
        setReferencedCell(null);
        super.delete();
    }

}
