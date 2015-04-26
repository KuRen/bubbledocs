package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.AssignBinaryFunctionToCell;

public class AssignBinaryFunctionToCellIntegrator extends BubbleDocsIntegrator {

    private AssignBinaryFunctionToCell service;

    public AssignBinaryFunctionToCellIntegrator(String token, int docId, String cellId, String expression) {
        this.service = new AssignBinaryFunctionToCell(token, docId, cellId, expression);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public String getResult() {
        return service.getResult();
    }

}
