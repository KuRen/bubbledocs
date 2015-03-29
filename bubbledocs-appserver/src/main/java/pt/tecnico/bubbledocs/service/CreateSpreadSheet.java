package pt.tecnico.bubbledocs.service;

import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class CreateSpreadSheet extends BubbleDocsService {
    private int sheetId;  // id of the new sheet
    private String userToken;
    private String name;
    private int rows;
    private int columns;

    public int getSheetId() {
        return sheetId;
    }

    public CreateSpreadSheet(String userToken, String name, int rows, int columns) {
        this.userToken = userToken;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (rows <= 0 || columns <= 0) {
            throw new InvalidArgumentException("The Spreadsheet dimensions must be positive");
        }

        if (name == null || name.isEmpty()) {
            throw new InvalidArgumentException("The Spreadsheet name can't be empty");
        }

        if (userToken == null || userToken.isEmpty()) {
            throw new InvalidArgumentException("The auth token can't be empty");
        }

        String username = getBubbleDocs().getManager().findUserByToken(userToken);

        if (username == null) {
            throw new UserNotInSessionException();
        }

        User user = getBubbleDocs().getUserByUsername(username);

        Spreadsheet spreadsheet = new Spreadsheet(rows, columns, name, user);

        this.sheetId = spreadsheet.getId();
    }
}
