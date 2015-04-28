package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.exception.BubbleDocsException;

public class ExportDocument extends BubbleDocsService {

    private byte[] docXML;
    private String userToken;
    private int docId;

    public byte[] getDocXML() {
        return docXML;
    }

    public ExportDocument(String userToken, int docId) {
        this.userToken = userToken;
        this.docId = docId;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {

        ExportSpreadsheetService service = new ExportSpreadsheetService(docId, userToken);
        service.execute();
        docXML = service.getResult();

        refreshToken(userToken);

    }
}
