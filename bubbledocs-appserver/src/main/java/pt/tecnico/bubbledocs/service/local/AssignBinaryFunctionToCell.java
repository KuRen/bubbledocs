package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Content;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Multiplication;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.Subtraction;
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
    private Content c1;
    private Content c2;
    private Content content;

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

        String function = parsedExpression[0];
        String args = parsedExpression[1];
        if (!args.matches("\\d+(;\\d+)?,\\d+(;\\d+)?\\)"))
            throw new InvalidArgumentException();

        String[] parsedArgs = args.split(",");
        if (parsedArgs.length != 2)
            throw new InvalidArgumentException();

        String arg1 = parsedArgs[0];
        String arg2 = parsedArgs[1].split("\\)")[0];

        Integer row;
        Integer column;

        try {
            if (arg1.contains(";")) {
                row = Integer.parseInt(arg1.split(";")[0]);
                column = Integer.parseInt(arg1.split(";")[1]);
                Cell c = ss.findCell(row, column);
                if (c == null)
                    c1 = new Reference(new Cell(ss, row, column));
                else
                    c1 = new Reference(c);

            } else
                c1 = new Literal(Integer.parseInt(arg1));

            if (arg2.contains(";")) {
                row = Integer.parseInt(arg2.split(";")[0]);
                column = Integer.parseInt(arg2.split(";")[1]);
                Cell c = ss.findCell(row, column);
                if (c == null)
                    c2 = new Reference(new Cell(ss, row, column));
                else
                    c2 = new Reference(c);

            } else
                c2 = new Literal(Integer.parseInt(arg2));

        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            throw new InvalidArgumentException();
        }

        switch (function) {
        case "ADD":
            content = new Addition(c1, c2);
            break;
        case "SUB":
            content = new Subtraction(c1, c2);
            break;
        case "MUL":
            content = new Multiplication(c1, c2);
            break;
        case "DIV":
            content = new Division(c1, c2);
            break;
        default:
            throw new InvalidArgumentException();
        }

        Cell c = ss.findCell(cellRow, cellColumn);
        c.setContent(content);

        result = c.asString();

        refreshToken(token);

    }

    public String getResult() {
        return result;
    }

}
