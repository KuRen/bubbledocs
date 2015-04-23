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

    public boolean canRead() {
        return getPermission().equals(PermissionType.READ) || getPermission().equals(PermissionType.WRITE);
    }

    public boolean canWrite() {
        return getPermission().equals(PermissionType.WRITE);
    }

}
