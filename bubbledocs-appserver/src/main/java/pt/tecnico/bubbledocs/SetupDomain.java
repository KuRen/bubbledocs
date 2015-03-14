package pt.tecnico.bubbledocs;

import pt.tecnico.bubbledocs.domain.*;

import pt.ist.fenixframework.Atomic;

public class SetupDomain {

    @Atomic
    static void populateDomain() {
    	
    //Would it be good to erase the curent data stored?
    	
        BubbleDocs bd = BubbleDocs.getInstance();
        
        User pf = new User("pf", "sub", "Paul Door");
        bd.addUsers(pf);
        User ra = new User("ra", "cor", "Step Rabbit");
        bd.addUsers(ra);
    
        Spreadsheet sheet = new Spreadsheet(300, 20, "Notas ES", pf);
        bd.addSpreadsheets(sheet);
        pf.addSpreadsheets(sheet);
        pf.addPermissions(new Permission());
       
        Cell cell = new Cell(3, 4);
        cell.setContent(new Literal(5));
        
        Cell cell2 = new Cell(1, 1);
        cell2.setContent(new Reference(new Cell(5, 6)));
        
        Cell cell3 = new Cell(5, 6);
        Content lit = new Literal(2);
        Content ref = new Reference(new Cell(3, 4));
        cell3.setContent(new Addition(lit, ref));
        
        Cell cell4 = new Cell(2, 2);
        Content ref2 = new Reference(new Cell(1, 1));
        Content ref3 = new Reference(new Cell(3, 4));
        cell4.setContent(new Division(ref2, ref3));
    }
}
