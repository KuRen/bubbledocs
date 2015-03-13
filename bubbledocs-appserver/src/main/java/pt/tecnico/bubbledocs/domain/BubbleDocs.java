package pt.tecnico.bubbledocs.domain;

import pt.ist.fenixframework.FenixFramework;

public class BubbleDocs extends BubbleDocs_Base {
    
    public static BubbleDocs getInstance() {
        BubbleDocs bd = FenixFramework.getDomainRoot().getBubbleDocs();
        if(bd == null) bd = new BubbleDocs();
        
        return bd;
    }
    
    private BubbleDocs() {
        FenixFramework.getDomainRoot().setBubbleDocs(this);
    }
    
}
