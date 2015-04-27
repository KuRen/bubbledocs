package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

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

        Spreadsheet ss = BubbleDocs.getInstance().getSpreadsheetById(docId);
        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);
        StoreRemoteServices remote = new StoreRemoteServices();

        try {
            remote.storeDocument(user.getName(), ss.getName(), docXML);
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }

        refreshToken(userToken);
    }
}
