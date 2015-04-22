package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.domain.BubbleDocs;

public abstract class BubbleDocsIntegrator {

    public abstract void execute();

    BubbleDocs getBubbleDocs() {
        return BubbleDocs.getInstance();
    }

}
