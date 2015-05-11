package pt.tecnico.bubbledocs.service.dto;

public class AuthenticationResult {

    private String key;
    private String ticket;

    public AuthenticationResult(String key, String ticket) {
        super();
        this.key = key;
        this.ticket = ticket;
    }

    public String getKey() {
        return key;
    }

    public String getTicket() {
        return ticket;
    }

}
