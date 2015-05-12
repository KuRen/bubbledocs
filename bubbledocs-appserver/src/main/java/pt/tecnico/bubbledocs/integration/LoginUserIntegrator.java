package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.dto.AuthenticationResult;
import pt.tecnico.bubbledocs.service.local.LoginUser;
import pt.tecnico.bubbledocs.service.local.SetPasswordWithUsernameService;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class LoginUserIntegrator extends BubbleDocsIntegrator {

    private String username;
    private String password;
    private String token;
    private String key;
    private String ticket;
    private IDRemoteServices idRemoteServices;

    public LoginUserIntegrator(String username, String password) {
        this.username = username;
        this.password = password;
        this.idRemoteServices = new IDRemoteServices();
    }

    @Override
    public void execute() {
        if (username == null || password == null || username.isEmpty() || password.isEmpty())
            throw new LoginBubbleDocsException();
        try {
            AuthenticationResult result = idRemoteServices.loginUser(username, password);
            new SetPasswordWithUsernameService(username, password).execute();
            LoginUser service = new LoginUser(username, password, result.getKey(), result.getTicket());
            service.execute();
            token = service.getUserToken();
            key = result.getKey();
            ticket = result.getTicket();
        } catch (RemoteInvocationException e) {
            try {
                LoginUser service = new LoginUser(username, password);
                service.execute();
                token = service.getUserToken();
            } catch (LoginBubbleDocsException ex) {
                throw new UnavailableServiceException();
            }
        }
    }

    public final String getUserToken() {
        return token;
    }

    public final String getUsername() {
        return username;
    }

    public final String getPassword() {
        return password;
    }

    public String getKey() {
        return key;
    }

    public String getTicket() {
        return ticket;
    }

}
