package pt.tecnico.bubbledocs.domain;

public class Permission extends Permission_Base {

    public Permission() {
        super();
    }

    public void delete() {
        this.setSpreadsheet(null);
        this.setUser(null);
        deleteDomainObject();
    }
}
