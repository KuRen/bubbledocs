package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.PermissionType;
import pt.tecnico.bubbledocs.domain.SessionManager;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NotLiteralException;
import pt.tecnico.bubbledocs.exception.TokenExpiredException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class AssignLiteralCell extends BubbleDocsService {
    private String result;

    private String tokenUser;
    private int docId;
    private String cellId;
    private String literal;

    public AssignLiteralCell(String accessUsername, int docId, String cellId, String literal) {
        this.tokenUser = accessUsername;
        this.docId = docId;
        this.cellId = cellId;
        this.literal = literal;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        BubbleDocs bd = getBubbleDocs();
        Spreadsheet ss = bd.getSpreadsheetById(docId);

        //para o caso da folha nao existir
        if (ss == null) {
            throw new InvalidSpreadSheetIdException();
        }

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

        try {
            String[] cell = this.cellId.split(";");
            cellRow = Integer.parseInt(cell[0]);
            cellColumn = Integer.parseInt(cell[1]);
        } catch (NumberFormatException e) {
            throw new InvalidArgumentException();
        }

        //para o caso da celula nao existir na folha (fora dos limites)
        if (cellRow > ss.getRows() || cellColumn > ss.getColumns()) {
            throw new CellOutOfRangeException();
        }

        //testa para o caso de nao ser dado um numero inteiro
        try {
            Integer.parseInt(literal);
        } catch (NumberFormatException e) {
            throw new NotLiteralException();
        }

        //se não existirem excepcoes atribui o literal a celula
        Cell c = ss.findCell(cellRow, cellColumn);
        Integer valor = Integer.parseInt(literal);
        Literal lit = new Literal(valor);
        c.setContent(lit);

        result = c.asString();
    }

    public String getResult() {
        return result;
    }
}
