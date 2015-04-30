package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.DeleteUser;

public class DeleteUserIntegrator extends BubbleDocsIntegrator {
    private DeleteUser service;

    public DeleteUserIntegrator(String userToken, String toDeleteUsername) {
        this.service = new DeleteUser(userToken, toDeleteUsername);
    }

    @Override
    public void execute() {
        service.execute();
    }

    public final String getUserToken() {
        return service.getUserToken();
    }

    public final String getToDeleteUsername() {
        return service.getToDeleteUsername();
    }

}
