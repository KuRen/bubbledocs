package pt.tecnico.bubbledocs;

import java.util.Random;

import pt.tecnico.bubbledocs.domain.Addition;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Division;
import pt.tecnico.bubbledocs.domain.Literal;
import pt.tecnico.bubbledocs.domain.Reference;
import pt.tecnico.bubbledocs.domain.Root;
import pt.tecnico.bubbledocs.service.AssignLiteralCell;
import pt.tecnico.bubbledocs.service.AssignReferenceCell;
import pt.tecnico.bubbledocs.service.CreateSpreadSheet;
import pt.tecnico.bubbledocs.service.CreateUser;

public class SetupDomain {

    static void populateDomain() {

        //Would it be good to erase the current data stored?
        BubbleDocs bd = BubbleDocs.getInstance();

        bd.addUsers(Root.getInstance());

        String pfToken = "pf" + new Random().nextInt(10);
        new CreateUser(pfToken, "pf", "sub", "Paul Door");
        String raToken = "ra" + new Random().nextInt(10);
        new CreateUser(raToken, "ra", "cor", "Step Rabbit");
        CreateSpreadSheet sheet = new CreateSpreadSheet(pfToken, "Notas ES", 300, 20);

        new AssignLiteralCell(pfToken, sheet.getSheetId(), "3;4", "5");
        new AssignReferenceCell(pfToken, sheet.getSheetId(), "1;1", "5;6");
        Cell cell56 = new Cell(bd.getSpreadsheetById(sheet.getSheetId()), 5, 6);

        Cell cell_ref34 = bd.getSpreadsheetById(sheet.getSheetId()).findCell(3, 4);
        cell56.setContent(new Addition(new Literal(2), new Reference(cell_ref34)));
        bd.getSpreadsheetById(sheet.getSheetId()).addCells(cell56);

        Cell cell22 = new Cell(bd.getSpreadsheetById(sheet.getSheetId()), 2, 2);
        Cell cell_ref11 = bd.getSpreadsheetById(sheet.getSheetId()).findCell(1, 1);
        cell22.setContent(new Division(new Reference(cell_ref11), new Reference(cell_ref34)));
        bd.getSpreadsheetById(sheet.getSheetId()).addCells(cell22);
    }
}
