package pt.tecnico.bubbledocs.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.ImportDocumentException;

public class ImportSpreadsheetService extends BubbleDocsService {

    private final byte[] document;

    private final String user;

    public ImportSpreadsheetService(byte[] doc, String username) {
        document = doc;
        user = username;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        Document jdomDocument;

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
        if (!(username.equals(user))) {
            System.err.println("User " + user + " can't import this spreadsheet");
            throw new ImportDocumentException();
        }
        getBubbleDocs().importSpreadsheetFromXML(rootElement);
    }
}
