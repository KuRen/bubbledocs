package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

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
        BubbleDocs bd = getBubbleDocs();
        Spreadsheet ss = bd.getSpreadsheetById(docId);

        if (ss == null)
            throw new InvalidSpreadSheetIdException();

        SessionManager sm = bd.getManager();
        String username;
        try {
            username = sm.findUserByToken(tokenUser);
        } catch (TokenExpiredException e) {
            throw new UnauthorizedOperationException();
        }

        if (username == null)
            throw new UserNotInSessionException();

        Permission permission = ss.findPermissionsForUser(username);

        if (permission == null || permission.getPermission() != PermissionType.WRITE)
            throw new UnauthorizedOperationException();

        Integer cellRow;
        Integer cellColumn;
        Integer refereceColumn;
        Integer referenceRow;

        try {
            String[] cell = this.cellId.split(";");
            cellRow = Integer.parseInt(cell[0]);
            cellColumn = Integer.parseInt(cell[1]);

            String[] reference = this.reference.split(";");
            referenceRow = Integer.parseInt(reference[0]);
            refereceColumn = Integer.parseInt(reference[1]);

        } catch (NumberFormatException e) {
            throw new InvalidArgumentException();
        }

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
