package pt.tecnico.bubbledocs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.FenixFramework;
import pt.ist.fenixframework.TransactionManager;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.ExportDocumentException;
import pt.tecnico.bubbledocs.exception.ImportDocumentException;
import pt.tecnico.bubbledocs.service.ExportSpreadsheetService;

public class BubbleApplication {

    public static void main(String[] args) throws NotSupportedException, SystemException {

        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean commit = false;
        tm.begin();

        try {
            BubbleDocs bd = BubbleDocs.getInstance();

            // initial state if it's empty
            if (bd.getUsersSet().isEmpty())
                setupDomain();

            System.out.println(" * * * * * * * * * * * * Started Bubble App * * * * * * * * * * * * ");

            // all registered users
            System.out.println("--------------------------- USERS ---------------------------");
            for (User u : bd.getUsersSet()) {
                System.out.println("> username: " + u.getUsername());
                System.out.println("> name: " + u.getName());
                System.out.println("> password: " + u.getPassword());
                System.out.println("----------------------------------------------");
            }

            // pf's spreadsheets: name
            System.out.println("> Spreadsheets (user: pf, attributes: name):");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    System.out.println("> name: " + s.getName());
                } catch (Exception ex) {
                    System.out.println("NONE");
                }
                System.out.println("-------------------");
            }

            // ra's spreadsheets: name
            System.out.println("> Spreadsheets (user: ra, attributes: name):");
            for (Spreadsheet s : bd.getUserByUsername("ra").getSpreadsheetsSet()) {
                try {
                    System.out.println("> name: " + s.getName());
                } catch (Exception ex) {
                    System.out.println("NONE");
                }
                System.out.println("-------------------");
            }

            // export pf's spreadsheets 
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    ExportSpreadsheetService expSS = new ExportSpreadsheetService(s);
                    expSS.execute();
                    byte[] docSS = expSS.getResult();
                    printDomainInXML(docSS);
                } catch (ExportDocumentException ex) {
                    System.err.println("Error while exporting to XML: " + ex.getMessage());
                }
            }

            // permanently removes spreadsheet "Notas ES"
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                if (s.getName().equals("Notas ES")) {
                    s.delete();
                    System.out.println("> Spreadsheet \"Notas ES\" of user pf permanently deleted from the database");
                    System.out.println("-------------------");
                }
            }

            // pf's spreadsheets: name and id
            System.out.println("> Spreadsheets (user: pf, attributes: name and id)");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    System.out.println("> name: " + s.getName());
                    System.out.println("> id:" + s.getId());
                } catch (Exception ex) {
                    System.out.println("NONE");
                }
                System.out.println("-------------------");
            }

            // *missing*
            /*
            System.out.println("Importing Spreadsheet");
            try {
                ImportSpreadsheetService importService = new ImportSpreadsheetService(docSS);
                importService.execute();
            } catch (ImportDocumentException ide) {
                System.err.println("Error importing document");
            }
            */

            System.out.println("> Spreadsheets (user: pf, attributes: name and id)");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    System.out.println("> name: " + s.getName());
                    System.out.println("> id:" + s.getId());
                } catch (Exception ex) {
                    System.out.println("NONE");
                }
                System.out.println("-------------------");
            }

            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    ExportSpreadsheetService expSS = new ExportSpreadsheetService(s);
                    expSS.execute();
                    byte[] docSS = expSS.getResult();
                    printDomainInXML(docSS);
                } catch (ExportDocumentException ex) {
                    System.err.println("Error while exporting to XML: " + ex.getMessage());
                }
            }

            bd = null;
            tm.commit();
            commit = true;

        } catch (SystemException | HeuristicRollbackException | HeuristicMixedException | RollbackException ex) {
            System.err.println("Transaction error: " + ex.getMessage());
        } finally {
            if (!commit) {
                try {
                    tm.rollback();
                } catch (SystemException ex) {
                    System.err.println("Error while rolling back: " + ex.getMessage());
                }
            }
        }

    }

    private static void setupDomain() {
        SetupDomain.populateDomain();
        System.out.println("//Finished populating domain");
    }

    public static void printDomainInXML(byte[] doc) {
        if (doc == null) {
            System.err.println("Null Document to print");
            return;
        }

        org.jdom2.Document jdomDoc;
        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);
        try {
            jdomDoc = builder.build(new ByteArrayInputStream(doc));
        } catch (JDOMException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new ImportDocumentException();
        }

        XMLOutputter xml = new XMLOutputter();
        xml.setFormat(Format.getPrettyFormat());
        System.out.println(xml.outputString(jdomDoc));
    }
}