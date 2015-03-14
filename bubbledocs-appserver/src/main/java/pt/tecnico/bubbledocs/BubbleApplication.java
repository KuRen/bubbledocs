package pt.tecnico.bubbledocs;

import pt.tecnico.bubbledocs.domain.*;
import pt.tecnico.bubbledocs.SetupDomain;

import pt.ist.fenixframework.Atomic;

public class BubbleApplication {

	public static void main(String[] args) {
        setupDomain();
    }
    
    private static void setupDomain() {
    	SetupDomain domainSetuper = new SetupDomain();
    	domainSetuper.populateDomain();
    }
}