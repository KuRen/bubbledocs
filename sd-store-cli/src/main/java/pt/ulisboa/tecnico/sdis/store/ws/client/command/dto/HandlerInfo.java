package pt.ulisboa.tecnico.sdis.store.ws.client.command.dto;


public class HandlerInfo {

    private String ticket;
    private String key;
    private String userId;

    public HandlerInfo(String ticket, String key, String userId) {
        super();
        this.ticket = ticket;
        this.key = key;
        this.userId = userId;
    }

    public String getTicket() {
        return ticket;
    }

    public String getKey() {
        return key;
    }

    public String getUserId() {
        return userId;
    }

}
