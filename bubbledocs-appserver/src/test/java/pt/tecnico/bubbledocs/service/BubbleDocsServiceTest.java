package pt.tecnico.bubbledocs.service;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.junit.After;
import org.junit.Before;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.core.WriteOnReadError;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;

// add needed import declarations

public class BubbleDocsServiceTest {

    @Before
    public void setUp() throws Exception {

        try {
            FenixFramework.getTransactionManager().begin(false);
            populate4Test();
        } catch (WriteOnReadError | NotSupportedException | SystemException e1) {
            e1.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        try {
            FenixFramework.getTransactionManager().rollback();
        } catch (IllegalStateException | SecurityException | SystemException e) {
            e.printStackTrace();
        }
    }

    // should redefine this method in the subclasses if it is needed to specify
    // some initial state
    public void populate4Test() {
    }

    // auxiliary methods that access the domain layer and are needed in the test classes
    // for defining the initial state and checking that the service has the expected behavior
    User createUser(String username, String password, String name) {
        BubbleDocs bd = BubbleDocs.getInstance();
        User user = new User(username, password, name);
        bd.addUsers(user);
        return null;
    }

    public Spreadsheet createSpreadSheet(User user, String name, int row, int column) {
        BubbleDocs bd = BubbleDocs.getInstance();
        Spreadsheet ss = new Spreadsheet(row, column, name, user);
        bd.addSpreadsheets(ss);
        return ss;
    }

    // returns a spreadsheet whose name is equal to name
    public Spreadsheet getSpreadSheet(String name) {
        BubbleDocs bd = BubbleDocs.getInstance();

        for (Spreadsheet ss : bd.getSpreadsheetsSet())
            if (ss.getName().equals(name))
                return ss;

        return null;
    }

    // returns the user registered in the application whose username is equal to username
    User getUserFromUsername(String username) {
        BubbleDocs bd = BubbleDocs.getInstance();

        for (User user : bd.getUsersSet())
            if (user.getUsername().equals(username))
                return user;

        return null;

    }

    // put a user into session and returns the token associated to it
    String addUserToSession(String username) {
        // add code here
        return null;

    }

    // remove a user from session given its token
    void removeUserFromSession(String token) {
        // add code here
    }

    // return the user registered in session whose token is equal to token
    User getUserFromSession(String token) {
        // add code here
        return null;
    }

}
