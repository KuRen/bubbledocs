package pt.tecnico.bubbledocs;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;

import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import pt.ist.fenixframework.Atomic;
import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.ExportDocumentException;
import pt.tecnico.bubbledocs.exception.ImportDocumentException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.service.ExportSpreadsheetService;
import pt.tecnico.bubbledocs.service.ImportSpreadsheetService;

public class BubbleApplication {

    private static BubbleDocs bd;

    public static void main(String[] args) throws NotSupportedException, SystemException {

        byte[] docSS = null;

        storeBubbleDocsInstance();

        populateDomain();
        log("Started BubbleDocs Application");

        log("List of all users registered in BubbleDocs");
        listAllUsers();

        log("List of Spreadsheets owned by pf");
        listSpreadsheetsOwnedBy("pf");

        log("List of Spreadsheets owned by ra");
        listSpreadsheetsOwnedBy("ra");

        log("XML conversion of pf's Spreadsheets");
        docSS = exportUserSpreadsheetsToXML("pf");

        log("Removal of 'Notas ES' Spreadsheet owned by pf from persistency");
        removeUserSpreadsheetsNamed("pf", "Notas ES");

        log("List of Spreadsheets owned by pf");
        listSpreadsheetsOwnedBy("pf");

        log("Import of the previously removed Spreadsheet");
        importFromXMLGivenUser(docSS, "pf");

        log("List of Spreadsheets owned by pf");
        listSpreadsheetsOwnedBy("pf");

        log("XML conversion of pf's Spreadsheets");
        docSS = exportUserSpreadsheetsToXML("pf");

    }

    @Atomic
    private static void importFromXMLGivenUser(byte[] docSS, String username) {
        try {
            ImportSpreadsheetService importService = new ImportSpreadsheetService(docSS, username);
            importService.execute();
            System.out.println("[Import] Spreadsheet successfully imported!");
        } catch (ImportDocumentException ide) {
            System.err.println("Error importing document");
        } catch (UnauthorizedUserException aue) {
            System.err.println("Error importing document: " + aue.getMessage());
        }
    }

    @Atomic
    private static void removeUserSpreadsheetsNamed(String username, String spreadsheetName) {
        try {
            for (Spreadsheet s : bd.getUserByUsername(username).getSpreadsheetsByName(spreadsheetName)) {
                String name = s.getName();
                s.delete();
                System.out.printf("[Spreadsheet] Name: '%s' deleted\n", name);
            }
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + " : " + e.getMessage());
        }
    }

    @Atomic
    private static byte[] exportUserSpreadsheetsToXML(String username) {
        byte[] docSS = null;
        for (Spreadsheet s : bd.getUserByUsername(username).getSpreadsheetsSet()) {
            try {
                ExportSpreadsheetService expSS =
                        new ExportSpreadsheetService(s.getId(), bd.getUserByUsername(username).getSession().getToken());
                expSS.execute();
                docSS = expSS.getResult();
                printDomainInXML(docSS);
            } catch (ExportDocumentException ex) {
                System.err.println("Error while exporting to XML: " + ex.getMessage());
            }
        }
        return docSS;
    }

    @Atomic
    private static void listSpreadsheetsOwnedBy(String username) {
        for (Spreadsheet s : bd.getUserByUsername(username).getSpreadsheetsSet()) {
            System.out.println("[Spreadsheet] Name: " + s.getName() + " | Id: " + s.getId());
        }
    }

    @Atomic
    private static void listAllUsers() {
        for (User user : bd.getUsersSet())
            System.out.printf("[User] Username: %10s | Name: %15s | Password: %10s\n", user.getUsername(), user.getName(),
                    user.getPassword());
    }

    @Atomic
    private static void storeBubbleDocsInstance() {
        bd = BubbleDocs.getInstance();
    }

    @Atomic
    private static void populateDomain() {
        log("Setting up domain");
        if (bd.getUsersSet().size() == 1) {
            SetupDomain.populateDomain();
            System.out.println("Domain populated.");
        } else {
            System.out.println("Populating skiped.");
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