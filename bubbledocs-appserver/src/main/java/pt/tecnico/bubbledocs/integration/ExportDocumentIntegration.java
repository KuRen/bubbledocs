package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.ExportDocument;

public class ExportDocumentIntegration extends BubbleDocsIntegrator {

    private ExportDocument service;

    public ExportDocumentIntegration(String token, int doc) {
        this.service = new ExportDocument(token, doc);
    }

    @Override
    public void execute() {
        service.execute();
    }
}