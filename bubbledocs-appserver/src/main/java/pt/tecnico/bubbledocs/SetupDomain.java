package pt.tecnico.bubbledocs;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Content;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Root;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;

public class SetupDomain {

    @Atomic
    static void populateDomain() {

        //Would it be good to erase the current data stored?
        BubbleDocs bd = BubbleDocs.getInstance();

        bd.addUsers(Root.getInstance());

        User pf = new User("pf", "sub", "Paul Door");
        new User("ra", "cor", "Step Rabbit");
        Spreadsheet sheet = new Spreadsheet(300, 20, "Notas ES", pf);

        Cell cell = new Cell(sheet, 3, 4);
        cell.setContent(new Literal(5));
        sheet.addCells(cell);

        Cell cell2 = new Cell(sheet, 1, 1);
        cell2.setContent(new Reference(new Cell(sheet, 5, 6)));
        sheet.addCells(cell2);

        Cell cell3 = new Cell(sheet, 5, 6);
        Content lit = new Literal(2);
        Content ref = new Reference(new Cell(sheet, 3, 4));
        cell3.setContent(new Addition(lit, ref));
        sheet.addCells(cell3);

        Cell cell4 = new Cell(sheet, 2, 2);
        Content ref2 = new Reference(new Cell(sheet, 1, 1));
        Content ref3 = new Reference(new Cell(sheet, 3, 4));
        cell4.setContent(new Division(ref2, ref3));
        sheet.addCells(cell4);

    }
}
