package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.exception.UserNotInSessionException;
import pt.tecnico.bubbledocs.service.remote.StoreRemoteServices;

public class ImportDocument extends BubbleDocsService {

    private final String docId;
    
    private final String userToken;

    public ImportDocument(String doc, String token) {
        docId = doc;
        userToken = token;
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        
        if (userToken == null || userToken.isEmpty()) {
            throw new InvalidArgumentException("The auth token can't be empty");
        }

        User user = getBubbleDocs().getSessionManager().findUserByToken(userToken);

        if (user == null) {
            throw new UserNotInSessionException();
        }
        
        StoreRemoteServices remote = new StoreRemoteServices();
        
        byte[] doc;
        
        try {
            doc = remote.loadDocument(user.getUsername(), docId);
        } catch(RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }
        ImportSpreadsheetService service = new ImportSpreadsheetService(doc,userToken);
        service.execute();
    }
}
