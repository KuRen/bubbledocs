package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.DeleteUser;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class DeleteUserIntegrator extends BubbleDocsIntegrator {
    
    private String userToken;
    private String toDeleteUsername;
    private IDRemoteServices idRemoteServices;

    public DeleteUserIntegrator(String userToken, String toDeleteUsername) {
        this.userToken = userToken;
        this.toDeleteUsername = toDeleteUsername;
        this.idRemoteServices = new IDRemoteServices();
    }

    @Override
    public void execute() {
        try {
            idRemoteServices.removeUser(toDeleteUsername);
        } catch (RemoteInvocationException rie) {
            throw new UnavailableServiceException();
        }
        new DeleteUser(userToken,toDeleteUsername).execute();
    }
    
}
