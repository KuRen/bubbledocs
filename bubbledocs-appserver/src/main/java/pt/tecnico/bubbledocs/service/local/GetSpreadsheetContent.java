package pt.tecnico.bubbledocs.service.local;

import java.util.stream.IntStream;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;

public class GetSpreadsheetContent extends BubbleDocsService {

    private String token;
    private Integer id;
    private String[][] matrix;

    public GetSpreadsheetContent(String token, Integer id) {
        this.token = token;
        this.id = id;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (id == null)
            throw new InvalidArgumentException();

        User user = getLoggedInUser(token);
        Spreadsheet spreadsheet = getBubbleDocs().getSpreadsheetById(id);

        if (!spreadsheet.hasReadPermission(user)) {
            throw new UnauthorizedUserException();
        }

        final int rows = spreadsheet.getRows().intValue();
        final int cols = spreadsheet.getColumns().intValue();

        matrix = new String[rows][cols];

        IntStream.range(0, rows).forEach(i -> IntStream.range(0, cols).forEach(j -> matrix[i][j] = ""));

        spreadsheet.getCellsSet().stream().forEach(c -> matrix[c.getRow() - 1][c.getColumn() - 1] = c.asString());
    }

    public String[][] getResult() {
        return matrix;
    }
}
