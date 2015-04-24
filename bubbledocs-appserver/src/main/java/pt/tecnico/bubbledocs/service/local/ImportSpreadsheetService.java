package pt.tecnico.bubbledocs.service.local;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.ImportDocumentException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;

public class ImportSpreadsheetService extends BubbleDocsService {

    private final byte[] document;

    private final String userToken;

    public ImportSpreadsheetService(byte[] doc, String token) {
        document = doc;
        userToken = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        Document jdomDocument;
        
        if (userToken == null || userToken.isEmpty()) {
            throw new InvalidArgumentException("The auth token can't be empty");
        }

        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);

        if (user == null) {
            throw new UserNotInSessionException();
        }

        SAXBuilder builder = new SAXBuilder();
        builder.setIgnoringElementContentWhitespace(true);

        try {
            jdomDocument = builder.build(new ByteArrayInputStream(document));
        } catch (JDOMException | IOException e) {
            e.printStackTrace();
            throw new ImportDocumentException();
        }

        Element rootElement = jdomDocument.getRootElement();

        String username = rootElement.getAttribute("owner").getValue();
        if (!(username.equals(user.getUsername()))) {
            throw new UnauthorizedUserException();
        }
        getBubbleDocs().importSpreadsheetFromXML(rootElement);
    }
}
