package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.AssignReferenceCell;

public class AssignReferenceCellIntegrator extends BubbleDocsIntegrator {
    private AssignReferenceCell service;

    public AssignReferenceCellIntegrator(String token, int docId, String cellId, String reference) {
        this.service = new AssignReferenceCell(token, docId, cellId, reference);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public final String getResult() {
        return service.getResult();
    }
}
