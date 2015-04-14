package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NotLiteralException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;

public class AssignLiteralCell extends BubbleDocsService {
    private String result;

    private String token;
    private int docId;
    private String cellId;
    private String literal;

    public AssignLiteralCell(String token, int docId, String cellId, String literal) {
        this.token = token;
        this.docId = docId;
        this.cellId = cellId;
        this.literal = literal;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (cellId == null || cellId.isEmpty())
            throw new InvalidArgumentException();

        if (literal == null || literal.isEmpty())
            throw new InvalidArgumentException();

        BubbleDocs bd = getBubbleDocs();
        Spreadsheet ss = bd.getSpreadsheetById(docId);

        if (ss == null) {
            throw new InvalidSpreadSheetIdException();
        }

        User user = getLoggedInUser(token);

        Permission permission = ss.findPermissionsForUser(user);

        if (permission == null || permission.getPermission() != PermissionType.WRITE)
            throw new UnauthorizedOperationException();

        Integer cellRow;
        Integer cellColumn;

        try {
            String[] cell = this.cellId.split(";");
            cellRow = Integer.parseInt(cell[0]);
            cellColumn = Integer.parseInt(cell[1]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException();
        }

        if (cellRow > ss.getRows() || cellColumn > ss.getColumns()) {
            throw new CellOutOfRangeException();
        }

        try {
            Integer.parseInt(literal);
        } catch (NumberFormatException e) {
            throw new NotLiteralException();
        }

        Cell c = ss.findCell(cellRow, cellColumn);
        Integer valor = Integer.parseInt(literal);
        Literal lit = new Literal(valor);
        c.setContent(lit);

        result = c.asString();

        refreshToken(token);
    }

    public String getResult() {
        return result;
    }
}
