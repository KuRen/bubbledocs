package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.GetSpreadsheetContent;

public class GetSpreadsheetContentIntegrator extends BubbleDocsIntegrator {

    private GetSpreadsheetContent service;

    public GetSpreadsheetContentIntegrator(String token, Integer id) {
        service = new GetSpreadsheetContent(token, id);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public String[][] getResult() {
        return service.getResult();
    }

}
