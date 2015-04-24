package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.service.local.LoginUser;

public class LoginUserIntegrator extends BubbleDocsIntegrator {
    private LoginUser service;

    public LoginUserIntegrator(String username, String password) {
        this.service = new LoginUser(username, password);
    }

    @Override
    public void execute() {
        service.execute();
    }

}
