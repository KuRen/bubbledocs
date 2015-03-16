package pt.tecnico.bubbledocs;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Root;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;

public class SetupDomain {

    static void populateDomain() {

        //Would it be good to erase the current data stored?
        BubbleDocs bd = BubbleDocs.getInstance();

        bd.addUsers(Root.getInstance());

        User pf = new User("pf", "sub", "Paul Door");
        new User("ra", "cor", "Step Rabbit");
        Spreadsheet sheet = new Spreadsheet(300, 20, "Notas ES", pf);
        pf.addSpreadsheets(sheet);

        Cell cell34 = new Cell(sheet, 3, 4);
        cell34.setContent(new Literal(5));
        sheet.addCells(cell34);

        Cell cell11 = new Cell(sheet, 1, 1);
        Cell cell56 = new Cell(sheet, 5, 6);
        cell11.setContent(new Reference(cell56));

        sheet.addCells(cell11);

        cell56.setContent(new Addition(new Literal(2), new Reference(cell34)));
        sheet.addCells(cell56);

        Cell cell22 = new Cell(sheet, 2, 2);
        cell22.setContent(new Division(new Reference(cell11), new Reference(cell34)));
        sheet.addCells(cell22);

    }
}
