package pt.tecnico.bubbledocs;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Content;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;

public class SetupDomain {

    @Atomic
    static void populateDomain() {

        //Would it be good to erase the current data stored?
        System.out.println("PopulateDomain entered");
        BubbleDocs bd = BubbleDocs.getInstance();
        System.out.println("got BubbleDocs instance");

        User pf = new User("pf", "sub", "Paul Door");
        bd.addUsers(pf);
        User ra = new User("ra", "cor", "Step Rabbit");
        bd.addUsers(ra);

        System.out.println("Added users!");
        Spreadsheet sheet = new Spreadsheet(300, 20, "Notas ES", pf);
        bd.addSpreadsheets(sheet);
        pf.addSpreadsheets(sheet);
        pf.addPermissions(new Permission());

        System.out.println("Added Spreadsheet and Permissions!");
        Cell cell = new Cell(3, 4);
        cell.setContent(new Literal(5));
        System.out.println("Added Cell 1!");
        Cell cell2 = new Cell(1, 1);
        cell2.setContent(new Reference(new Cell(5, 6)));
        System.out.println("Added Cell 2!");

        Cell cell3 = new Cell(5, 6);
        Content lit = new Literal(2);
        Content ref = new Reference(new Cell(3, 4));
        cell3.setContent(new Addition(lit, ref));
        System.out.println("Added Cell 3!");

        Cell cell4 = new Cell(2, 2);
        Content ref2 = new Reference(new Cell(1, 1));
        Content ref3 = new Reference(new Cell(3, 4));
        cell4.setContent(new Division(ref2, ref3));
        System.out.println("Added Cell 4!");
    }
}
