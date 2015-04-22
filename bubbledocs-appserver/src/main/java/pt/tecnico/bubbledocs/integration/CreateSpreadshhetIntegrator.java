package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;

public class CreateSpreadshhetIntegrator extends BubbleDocsIntegrator {

    private int sheetId;
    private String token;
    private String name;
    private int rows;
    private int columns;

    public int getSheetId() {
        return sheetId;
    }

    public CreateSpreadshhetIntegrator(String userToken, String name, int rows, int columns) {
        this.token = userToken;
        this.name = name;
        this.rows = rows;
        this.columns = columns;
    }

    @Override
    public void execute() {
        new CreateSpreadSheet(token, name, rows, columns).execute();
    }

}
