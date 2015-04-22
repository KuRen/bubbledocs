package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.AssignLiteralCell;

public class AssignLiteralCellIntegrator extends BubbleDocsIntegrator {

    private AssignLiteralCell service;

    public AssignLiteralCellIntegrator(String token, int docId, String cellId, String literal) {
        this.service = new AssignLiteralCell(token, docId, cellId, literal);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public String getResult() {
        return service.getResult();
    }
}
