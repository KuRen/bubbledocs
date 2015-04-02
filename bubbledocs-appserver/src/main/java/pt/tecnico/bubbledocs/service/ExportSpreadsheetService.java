package pt.tecnico.bubbledocs.service;

import java.io.UnsupportedEncodingException;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Permission;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.ExportDocumentException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.InvalidSpreadSheetIdException;
import pt.tecnico.bubbledocs.exception.NonExistingSpreadsheetException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.xml.BaseXMLWriter;
import pt.tecnico.bubbledocs.xml.XMLWriter;

public class ExportSpreadsheetService extends BubbleDocsService {

    private int spreadsheetId;
    private XMLWriter writer;
    private byte[] document;
    private String userToken;

    public ExportSpreadsheetService(int Id, String token) {
        this(Id, new BaseXMLWriter(), token);
    }

    public ExportSpreadsheetService(int Id, XMLWriter xmlWriter, String token) {
        spreadsheetId = Id;
        writer = xmlWriter;
        userToken = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        if (spreadsheetId <= 0) {
            throw new InvalidSpreadSheetIdException();
        }

        Spreadsheet ss = BubbleDocs.getInstance().getSpreadsheetById(spreadsheetId);

        if (ss == null) {
            throw new NonExistingSpreadsheetException();
        }

        if (userToken == null || userToken.isEmpty()) {
            throw new InvalidArgumentException("The auth token can't be empty");
        }

        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);

        if (user == null) {
            throw new UserNotInSessionException();
        }

        Permission permission = ss.findPermissionsForUser(user);

        if (permission == null && !(ss.getOwner().getUsername().equals(user)))
            throw new UnauthorizedUserException();

        Document jdomDoc = new Document();

        jdomDoc.setRootElement(ss.accept(writer));

        XMLOutputter xml = new XMLOutputter();
        try {
            document = xml.outputString(jdomDoc).getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new ExportDocumentException();
        }

        refreshToken(userToken);

    }

    public byte[] getResult() {
        return document;
    }

}
