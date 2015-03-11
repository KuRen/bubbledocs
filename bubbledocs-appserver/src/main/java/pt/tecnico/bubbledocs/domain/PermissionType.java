package pt.tecnico.bubbledocs.domain;

public enum PermissionType {

    READ,

    WRITE;

    public String getName() {
        return name();
    }

    public String getQualifiedName() {
        return PermissionType.class.getSimpleName() + "." + name();
    }

    public String getFullyQualifiedName() {
        return PermissionType.class.getName() + "." + name();
    }

}
