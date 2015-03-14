package pt.tecnico.bubbledocs.service;

import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import jvstm.Atomic;

public abstract class BubbleDocsService {
	
	@Atomic
	public final void execute() throws BubbleDocsException {
		dispatch();
	}
	
	static BubbleDocs getPhoneBook() {
        return FenixFramework.getDomainRoot().getBubbleDocs();
    }

    protected abstract void dispatch() throws BubbleDocsException;
    
}
