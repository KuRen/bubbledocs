package pt.tecnico.bubbledocs.service;

import jvstm.Atomic;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public abstract class BubbleDocsService {

	@Atomic
	public final void execute() throws BubbleDocsException {
		dispatch();
	}

	static BubbleDocs getBubbleDocs() {
		return FenixFramework.getDomainRoot().getBubbleDocs();
	}

	protected abstract void dispatch() throws BubbleDocsException;

}
