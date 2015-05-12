package pt.tecnico.bubbledocs.integration;

import pt.tecnico.bubbledocs.exception.LoginBubbleDocsException;
import pt.tecnico.bubbledocs.exception.RemoteInvocationException;
import pt.tecnico.bubbledocs.exception.UnavailableServiceException;
import pt.tecnico.bubbledocs.service.local.GetUsernameForTokenService;
import pt.tecnico.bubbledocs.service.local.RenewPassword;
import pt.tecnico.bubbledocs.service.local.SetPasswordService;
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

        SetPasswordService resetPasswordService = new SetPasswordService(token, service.getRemovedPassword());

        try {
            idRemoteServices.renewPassword(username);
        } catch (RemoteInvocationException rie) {
            resetPasswordService.execute();;
            throw new UnavailableServiceException();
        } catch (LoginBubbleDocsException lbe) {
            resetPasswordService.execute();
            throw lbe;
        }
    }

}
