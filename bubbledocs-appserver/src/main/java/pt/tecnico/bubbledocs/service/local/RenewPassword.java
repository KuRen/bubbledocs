package pt.tecnico.bubbledocs.service.local;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.BubbleDocsException;
import pt.tecnico.bubbledocs.exception.InvalidArgumentException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.BubbleDocsService;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPassword extends BubbleDocsService {

    private String token;
    private IDRemoteServices idRemoteServices;

    public RenewPassword(String token) {
        this.token = token;
        this.idRemoteServices = new IDRemoteServices();
    }

    @Override
    protected void dispatch() throws BubbleDocsException {
        if (token == null || token.isEmpty()) {
            throw new InvalidArgumentException();
        }

        User user = getLoggedInUser(token);

        try {
            idRemoteServices.renewPassword(user.getUsername());
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }

        user.setPassword(null);
    }
}
