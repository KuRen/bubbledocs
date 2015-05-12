package pt.tecnico.bubbledocs.service.dto;

public class UserInfo {

    private String username;
    private String name;
    private String email;
    private String password;

    public UserInfo(String username, String name, String email, String password) {
        super();
        this.username = username;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}
