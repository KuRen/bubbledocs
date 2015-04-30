package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.CreateUser;
import pt.tecnico.bubbledocs.service.local.DeleteUser;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class CreateUserIntegrator extends BubbleDocsIntegrator {
    private CreateUser createUserService;
    private DeleteUser deleteUserService;
    private IDRemoteServices idRemoteServices;
    private String userToken;
    private String newUsername;
    private String email;

    public CreateUserIntegrator(String userToken, String newUsername, String email, String name) {
        this.createUserService = new CreateUser(userToken, newUsername, email, name);
        this.idRemoteServices = new IDRemoteServices();
        this.userToken = userToken;
        this.newUsername = newUsername;
        this.email = email;
    }

    @Override
    public void execute() {
        createUserService.execute();

        try {
            idRemoteServices.createUser(newUsername, email);
        } catch (RemoteInvocationException rie) {
            deleteUserService = new DeleteUser(userToken, newUsername);
            deleteUserService.execute();
            throw new UnavailableServiceException();
        }
    }

}
