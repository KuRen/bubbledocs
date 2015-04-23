package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;

public class AssignBinaryFunctionToCell extends BubbleDocsService {

    private String result;

    private String token;
    private int docId;
    private String cellId;
    private String expression;
    private String function;
    private String args;

    public AssignBinaryFunctionToCell(String token, int docId, String cellId, String expression) {
        this.token = token;
        this.docId = docId;
        this.cellId = cellId;
        this.expression = expression;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (cellId == null || cellId.isEmpty())
            throw new InvalidArgumentException();

        if (token == null || token.isEmpty())
            throw new InvalidArgumentException();

        if (expression == null || expression.isEmpty())
            throw new InvalidArgumentException();

        BubbleDocs bd = getBubbleDocs();
        Spreadsheet ss = bd.getSpreadsheetById(docId);

        if (ss == null)
            throw new InvalidSpreadSheetIdException();

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

        String[] parsedExpression = this.expression.split("\\(");

        if (parsedExpression.length != 2)
            throw new InvalidArgumentException();

        function = parsedExpression[0];
        args = parsedExpression[1];
    }

    public String getResult() {
        return result;
    }

}
