package pt.tecnico.bubbledocs.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import pt.tecnico.bubbledocs.domain.Cell;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NotLiteralException;
import pt.tecnico.bubbledocs.exception.UnauthorizedOperationException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class AssignLiteralCellTest extends BubbleDocsServiceTest {

    private String token;
    private String notOwnerToken;
    private int id;

    private static final String USERNAME = "ars";
    private static final String PASSWORD = "ars";
    private static final String NOT_OWNER_USERNAME = "src";
    private static final String NOT_OWNER_PASSWORD = "src";
    private static final String SPREADSHEET_NAME = "ss-name";
    private static final int COLUMNS = 5;
    private static final int ROWS = 5;
    private static final String VALUE = "42";

    @Override
    public void populate4Test() {
        createUser(NOT_OWNER_USERNAME, NOT_OWNER_PASSWORD, "António Rito Silva");
        notOwnerToken = addUserToSession(NOT_OWNER_USERNAME);

        User ars = createUser(USERNAME, PASSWORD, "António Rito Silva");
        token = addUserToSession(USERNAME);

        Spreadsheet ss = createSpreadSheet(ars, SPREADSHEET_NAME, ROWS, COLUMNS);
        id = ss.getId();

        //cria as celulas
        Cell c1 = new Cell(ss, 1, 1);

        ss.addCells(c1);
    }

    @Test
    public void success() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "1;1", VALUE);
        service.execute();

        Cell c1 = getSpreadSheet(SPREADSHEET_NAME).findCell(1, 1);

        assertNotNull(c1.getContent());
        assertEquals(c1.getContent().getClass().getSimpleName(), "Literal");
        assertTrue(Integer.parseInt(VALUE) == c1.getContent().getValue());
    }

    //testa para o caso do user nao ter permissoes de escrita
    @Test(expected = UnauthorizedOperationException.class)
    public void unauthorizedToken() {
        AssignLiteralCell service = new AssignLiteralCell(notOwnerToken, id, "1;1", VALUE);
        service.execute();
    }

    // testa para o caso do utilizador nao estar na sessao
    @Test(expected = UserNotInSessionException.class)
    public void absentUser() {
        AssignLiteralCell service = new AssignLiteralCell("invalid", id, "2;2", VALUE);
        service.execute();
    }

    //testa para o caso de nao ser dado um numero
    @Test(expected = NotLiteralException.class)
    public void invalidType() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "1;1", "qwerty");
        service.execute();
    }

    //testa para o caso de nao ser dado um numero inteiro
    @Test(expected = NotLiteralException.class)
    public void invalidType2() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "1;1", "-2.3");
        service.execute();
    }

    //testa para o caso da String referente a celula nao ter o formato esperado
    @Test(expected = InvalidArgumentException.class)
    public void invalidArgumentCell() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "qwerty", VALUE);
        service.execute();
    }

    //testa para o caso da celula nao existir na folha (fora dos limites)
    @Test(expected = CellOutOfRangeException.class)
    public void outOfRange() {
        AssignLiteralCell service = new AssignLiteralCell(token, id, "8;8", VALUE);
        service.execute();
    }

    //testa para o caso da folha nao existir
    @Test(expected = InvalidSpreadSheetIdException.class)
    public void invalidSpreadSheetId() {
        AssignLiteralCell service = new AssignLiteralCell(token, 0, "1;1", VALUE);
        service.execute();
    }
}