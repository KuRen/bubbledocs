package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.local.GetUserInfoService;
import pt.tecnico.bubbledocs.service.local.GetUsernameForTokenService;
import pt.tecnico.bubbledocs.service.local.ImportSpreadsheetService;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocumentIntegrator extends BubbleDocsIntegrator {

    private String docName;
    private String userToken;

    public ImportDocumentIntegrator(String doc, String token) {
        docName = doc;
        userToken = token;
    }

    @Override
    public void execute() {

        if (docName == null || docName.isEmpty()) {
            throw new InvalidArgumentException("The document name can't be empty");
        }

        if (userToken == null || userToken.isEmpty()) {
            throw new InvalidArgumentException("The auth token can't be empty");
        }

        GetUsernameForTokenService getUsernameService = new GetUsernameForTokenService(userToken);

        getUsernameService.execute();

        String username = getUsernameService.getUsername();

        GetUserInfoService getUserService = new GetUserInfoService(username);

        getUserService.execute();

        User user = getUserService.getUser();

        if (user == null) {
            throw new UserNotInSessionException();
        }

        StoreRemoteServices remote = new StoreRemoteServices();

        byte[] doc;

        try {
            doc = remote.loadDocument(username, docName);
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }
        ImportSpreadsheetService service = new ImportSpreadsheetService(doc, userToken);
        service.execute();
    }
}
