package pt.tecnico.bubbledocs;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
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

        Cell cell34 = new Cell(sheet, 3, 4);
        cell34.setContent(new Literal(5));
        sheet.addCells(cell34);

        //Sem o resto funciona até aqui

        Reference ref34 = new Reference(cell34);
        ref34.setReferencedCell(cell34);

        Cell cell56 = new Cell(sheet, 5, 6);
        cell56.setContent(ref34);
        sheet.addCells(cell56);

        /*
        Cell cell11 = new Cell(sheet, 1, 1);
        Cell cell56 = new Cell(sheet, 5, 6);
        Cell cell22 = new Cell(sheet, 2, 2);
        /*
        cell34.setContent(new Literal(5));
        sheet.addCells(cell34);
        /*
        Reference ref34 = new Reference(cell34);
        ref34.setCell(cell34);
        /*
        cell56.setContent(new Addition(new Literal(2), new Reference(cell34)));
        sheet.addCells(cell56);

        /*
        cell11.setContent(new Reference(cell56));
        sheet.addCells(cell11);

        /*
        sheet.addCells(cell56);
        sheet.addCells(cell22);
        
        
        //Reference ref34 = new Reference(cell34);

        cell34.setContent(new Literal(5));
        cell11.setContent(new Reference(cell56));
        cell56.setContent(new Addition(new Literal(2), new Reference(cell34)));
        cell22.setContent(new Division(new Reference(cell11), new Reference(cell34)));

        /* Cell cell = new Cell(sheet, 3, 4);
         cell.setContent(new Literal(5));
         sheet.addCells(cell);

         Cell cell3 = new Cell(sheet, 5, 6);
         Content lit = new Literal(2);
         Content ref = new Reference(new Cell(sheet, 3, 4));
         cell3.setContent(new Addition(lit, ref));
         sheet.addCells(cell3);

         /*
         Cell cell2 = new Cell(sheet, 1, 1);
         cell2.setContent(new Reference(new Cell(sheet, 5, 6)));
         sheet.addCells(cell2);
         /*
         Cell cell3 = new Cell(sheet, 5, 6);
         Content lit = new Literal(2);
         Content ref = new Reference(new Cell(sheet, 3, 4));
         cell3.setContent(new Addition(lit, ref));
         sheet.addCells(cell3);

         Cell cell4 = new Cell(sheet, 2, 2);
         Content ref2 = new Reference(new Cell(sheet, 1, 1));
         Content ref3 = new Reference(new Cell(sheet, 3, 4));
         cell4.setContent(new Division(ref2, ref3));
         sheet.addCells(cell4);*/

        System.out.println("End Populate");

    }
}
