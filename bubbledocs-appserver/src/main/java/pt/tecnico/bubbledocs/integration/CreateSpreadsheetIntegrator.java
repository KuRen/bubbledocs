package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.CreateSpreadSheet;

public class CreateSpreadsheetIntegrator extends BubbleDocsIntegrator {

    private CreateSpreadSheet service;

    public CreateSpreadsheetIntegrator(String token, String name, int rows, int columns) {
        this.service = new CreateSpreadSheet(token, name, rows, columns);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public int getSheetId() {
        return service.getSheetId();
    }
}
