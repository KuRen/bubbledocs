package pt.tecnico.bubbledocs.domain;

public class User extends User_Base {
    
    public User(String username, String password, String name) {
        super();
        setUsername(username);
        setPassword(password);
        setName(name);
    }
}
