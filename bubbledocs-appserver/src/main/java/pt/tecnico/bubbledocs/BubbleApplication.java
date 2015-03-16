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
import pt.tecnico.bubbledocs.service.ImportSpreadsheetService;

public class BubbleApplication {

    public static void main(String[] args) throws NotSupportedException, SystemException {

        TransactionManager tm = FenixFramework.getTransactionManager();
        boolean commit = false;
        tm.begin();

        try {
            BubbleDocs bd = BubbleDocs.getInstance();
            byte[] docSS = null;

            // initial state if it's empty
            if (bd.getUsersSet().isEmpty()) {
                log("Setting up domain");
                SetupDomain.populateDomain();
                log("Domain populated");
            }

            log("Started Bubble Application");

            // all registered users
            log("Registered Users");
            for (User u : bd.getUsersSet()) {
                System.out.printf("[User] Username: %s | Name: %s | Password: %s\n", u.getUsername(), u.getName(),
                        u.getPassword());
            }

            // pf's spreadsheets: name
            log("Spreadsheets owned by pf");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                System.out.println("[Spreadsheet] Name: " + s.getName());
            }

            // ra's spreadsheets: name
            log("Spreadsheets owned by ra");
            for (Spreadsheet s : bd.getUserByUsername("ra").getSpreadsheetsSet()) {
                System.out.println("[Spreadsheet] Name: " + s.getName());
            }

            // export pf's spreadsheets 
            log("XML Export");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    ExportSpreadsheetService expSS = new ExportSpreadsheetService(s);
                    expSS.execute();
                    docSS = expSS.getResult();
                    printDomainInXML(docSS);
                } catch (ExportDocumentException ex) {
                    System.err.println("Error while exporting to XML: " + ex.getMessage());
                }
            }

            // permanently removes spreadsheet "Notas ES"
            log("Delete \"Notas ES\" Spreadsheet from User pf");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                if (s.getName().equals("Notas ES")) {
                    s.delete();
                    System.out.println("[Spreadsheet] Name: \"Notas ES\" deleted.");
                }
            }

            // pf's spreadsheets: name and id
            log("Spreadsheets owned by pf");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                System.out.println("[Spreadsheet] Name: " + s.getName() + " | Id: " + s.getId());
            }

            log("Import from XML");
            try {
                ImportSpreadsheetService importService = new ImportSpreadsheetService(docSS);
                importService.execute();
                System.out.println("Spreadsheet successfully imported!");
            } catch (ImportDocumentException ide) {
                System.err.println("Error importing document");
            }

            log("Spreadsheets owned by pf");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                System.out.println("[Spreadsheet] Name: " + s.getName() + " | Id: " + s.getId());
            }

            log("XML Export");
            for (Spreadsheet s : bd.getUserByUsername("pf").getSpreadsheetsSet()) {
                try {
                    ExportSpreadsheetService expSS = new ExportSpreadsheetService(s);
                    expSS.execute();
                    docSS = expSS.getResult();
                    printDomainInXML(docSS);
                } catch (ExportDocumentException ex) {
                    System.err.println("Error while exporting to XML: " + ex.getMessage());
                }
            }

            bd = null;
            log("Ready to commit");
            tm.commit();
            log("Done! 42");
            commit = true;

        } catch (SystemException | HeuristicRollbackException | HeuristicMixedException | RollbackException ex) {
            System.err.println("Transaction error! :(");
            System.err.println("ex.toString() : " + ex);
            System.err.println("ex.getMessage() : " + ex.getMessage());
            System.err.println("ex.getClass().getName() : " + ex.getClass().getName());
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

    public static void log(String msg) {
        System.out.println("");
        System.out.println("==============================================================================================");
        System.out.println("== " + msg);
        System.out.println("==============================================================================================");
        System.out.println("");
    }
}