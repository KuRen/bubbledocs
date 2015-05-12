package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.dto.UserInfo;
import pt.tecnico.bubbledocs.service.local.ExportSpreadsheetService;
import pt.tecnico.bubbledocs.service.local.GetUserInfoService;
import pt.tecnico.bubbledocs.service.local.GetUsernameForTokenService;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ExportDocumentIntegrator extends BubbleDocsIntegrator {

    private int docId;
    private String userToken;
    private byte[] xml;

    public ExportDocumentIntegrator(String token, int doc) {
        docId = doc;
        userToken = token;
    }

    public byte[] getDocXML() {
        return xml;
    }

    @Override
    public void execute() {
        ExportSpreadsheetService service = new ExportSpreadsheetService(docId, userToken);
        service.execute();
        xml = service.getResult();

        GetUsernameForTokenService username = new GetUsernameForTokenService(userToken);
        username.execute();

        GetUserInfoService userInfoService = new GetUserInfoService(username.getUsername());
        userInfoService.execute();
        UserInfo user = userInfoService.getUser();

        StoreRemoteServices remote = new StoreRemoteServices();

        try {
            remote.storeDocument(user.getName(), new Integer(docId).toString(), getDocXML());
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }

    }
}