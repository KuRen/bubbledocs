package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.ImportDocument;

public class ImportDocumentIntegrator extends BubbleDocsIntegrator {
    private ImportDocument service;

    public ImportDocumentIntegrator(String doc, String token) {
        this.service = new ImportDocument(doc, token);
    }

    @Override
    public void execute() {
        service.execute();
    }
}
