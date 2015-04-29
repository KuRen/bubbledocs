package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.domain.BubbleDocs;
import pt.tecnico.bubbledocs.domain.Spreadsheet;
import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.ExportDocument;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegrator extends BubbleDocsIntegrator {

    private ExportDocument service;
    private int docId;
    private String userToken;

    public ExportDocumentIntegrator(String token, int doc) {
        this.service = new ExportDocument(token, doc);
    }

    public byte[] getDocXML() {
        //TODO
        return null;
    }

    @Override
    public void execute() {
        service.execute();

        Spreadsheet ss = BubbleDocs.getInstance().getSpreadsheetById(docId);
        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);
        StoreRemoteServices remote = new StoreRemoteServices();

        try {
            remote.storeDocument(user.getName(), ss.getName(), service.getDocXML());
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }

    }
}