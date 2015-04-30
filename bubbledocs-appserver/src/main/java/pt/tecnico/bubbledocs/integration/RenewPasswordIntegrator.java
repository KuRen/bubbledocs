package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.domain.User;
import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.GetUserInfoService;
import pt.tecnico.bubbledocs.service.local.GetUsernameForTokenService;
import pt.tecnico.bubbledocs.service.local.RenewPassword;
import pt.tecnico.bubbledocs.service.remote.IDRemoteServices;

public class RenewPasswordIntegrator extends BubbleDocsIntegrator {

    private RenewPassword service;
    private IDRemoteServices idRemoteServices;
    private String token;

    public RenewPasswordIntegrator(String token) {
        this.token = token;
        this.service = new RenewPassword(token);
        this.idRemoteServices = new IDRemoteServices();
    }

    @Override
    public void execute() {
        service.execute();

        GetUsernameForTokenService getUsernameService = new GetUsernameForTokenService(token);

        getUsernameService.execute();

        String username = getUsernameService.getUsername();

        GetUserInfoService getUserService = new GetUserInfoService(username);

        getUserService.execute();

        User user = getUserService.getUser();

        try {
            idRemoteServices.renewPassword(username);
        } catch (RemoteInvocationException rie) {
            user.setPassword(service.getRemovedPassword());
            throw new UnavailableServiceException();
        } catch (LoginBubbleDocsException lbe) {
            user.setPassword(service.getRemovedPassword());
            throw lbe;
        }
    }

}
