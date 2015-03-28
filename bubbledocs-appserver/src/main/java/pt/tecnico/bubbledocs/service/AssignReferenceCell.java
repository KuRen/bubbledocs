package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;

public class AssignReferenceCell extends BubbleDocsService {
    private String result;

    private String tokenUser;
    private int docId;
    private String cellId;
    private String reference;

    public AssignReferenceCell(String tokenUser, int docId, String cellId, String reference) {
        this.tokenUser = tokenUser;
        this.docId = docId;
        this.cellId = cellId;
        this.reference = reference;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        // TODO: verify token and permissions
        String[] cell = this.cellId.split(";");
        Integer cellRow = Integer.parseInt(cell[0]);
        Integer cellColumn = Integer.parseInt(cell[1]);

        String[] reference = this.reference.split(";");
        Integer referenceRow = Integer.parseInt(reference[0]);
        Integer refereceColumn = Integer.parseInt(reference[1]);

        BubbleDocs bd = getBubbleDocs();
        Spreadsheet ss = bd.getSpreadsheetById(docId);

        if (ss == null)
            throw new InvalidSpreadSheetIdException();

        if (cellRow > ss.getRows() || referenceRow > ss.getRows() || cellColumn > ss.getColumns()
                || refereceColumn > ss.getColumns())
            throw new CellOutOfRangeException();

        Cell c = ss.findCell(cellRow, cellColumn);
        Cell cr = ss.findCell(referenceRow, refereceColumn);

        Reference ref = new Reference(cr);
        c.setContent(ref);

        result = c.asString();
    }

    public final String getResult() {
        return result;
    }
}
