package pt.tecnico.bubbledocs.domain;

import org.jdom2.Element;
import org.joda.time.DateTime;

import pt.tecnico.bubbledocs.exception.CellOutOfRangeException;
import pt.tecnico.bubbledocs.exception.UnauthorizedUserException;
import pt.tecnico.bubbledocs.xml.XMLWriter;
import pt.tecnico.bubbledocs.xml.XMLable;

public class Spreadsheet extends Spreadsheet_Base implements XMLable {

    protected Spreadsheet() {
        super();
    }

    public Spreadsheet(Integer rows, Integer columns, String name, User user) {
        super();
        BubbleDocs bd = BubbleDocs.getInstance();
        setId(bd.getSheetsID());
        bd.setSheetsID(bd.getSheetsID() + 1);
        setBubbledocs(bd);

        setRows(rows);
        setColumns(columns);
        setName(name);
        setOwner(user);

        Permission permission = new Permission();
        permission.setPermission(PermissionType.WRITE);
        permission.setUser(user);
        addPermissions(permission);

        DateTime date = new DateTime();
        setCreationDate(date);
    }

    public void importFromXML(Element spreadsheetElement) {

        Integer rows = new Integer(spreadsheetElement.getAttribute("rows").getValue());
        setRows(rows);
        Integer columns = new Integer(spreadsheetElement.getAttribute("columns").getValue());
        setColumns(columns);
        String name = spreadsheetElement.getAttribute("name").getValue();
        setName(name);
        DateTime date = new DateTime();
        setCreationDate(date);
        String username = spreadsheetElement.getAttribute("owner").getValue();
        BubbleDocs bd = BubbleDocs.getInstance();
        User owner = bd.getUserByUsername(username);
        setOwner(owner);
        Permission permission = new Permission();
        permission.setPermission(PermissionType.WRITE);
        permission.setUser(owner);
        addPermissions(permission);
        setId(bd.getSheetsID());
        bd.setSheetsID(bd.getSheetsID() + 1);

        Element cells = spreadsheetElement.getChild("Cells");
        for (Element cellElement : cells.getChildren("Cell")) {
            Integer row = Integer.parseInt(cellElement.getAttribute("row").getValue());
            Integer column = Integer.parseInt(cellElement.getAttribute("column").getValue());

            Cell cell = findCell(row, column);

            if (cell != null) {
                cell.importFromXML(cellElement);
                continue;
            }

            cell = new Cell();
            addCells(cell);
            cell.importFromXML(cellElement);

        }
    }

    @Override
    public void addCells(Cell cell) {
        if (cell.getRow() != null && cell.getColumn() != null)
            if (cell.getRow() > getRows() || cell.getColumn() > getColumns())
                throw new CellOutOfRangeException();

        super.addCells(cell);
    }

    /**
     * @Deprecated This method does not validate if the User can perform this operation
     */
    @Override
    @Deprecated
    public void addPermissions(Permission permission) {
        super.addPermissions(permission);
    }

    public void addPermissions(Permission permissions, User user) {
        if (getOwner().equals(user)) {
            super.addPermissions(permissions);
            return;
        }

        Permission permission = findPermissionsForUser(user);

        if (permission == null || permission.getPermission() != PermissionType.WRITE) {
            throw new UnauthorizedUserException();
        }

        super.addPermissions(permissions);
    }

    public Permission findPermissionsForUser(User user) {
        return findPermissionsForUser(user.getUsername());
    }

    public Permission findPermissionsForUser(String username) {
        for (Permission p : getPermissionsSet()) {
            if (p.getUser().getUsername().equals(username)) {
                return p;
            }
        }
        return null;
    }

    public Cell findCell(Integer row, Integer column) {
        for (Cell c : getCellsSet())
            if (c.getRow().equals(row) && c.getColumn().equals(column))
                return c;
        return null;
    }

    @Override
    public Element accept(XMLWriter writer) {
        return writer.visit(this);
    }

    public void delete() {
        for (Cell c : getCellsSet())
            c.delete();
        for (Permission p : this.getPermissionsSet())
            p.delete();

        this.setOwner(null);
        this.setBubbledocs(null);

        deleteDomainObject();
    }

    public boolean hasReadPermission(User user) {
        return findPermissionsForUser(user) != null && findPermissionsForUser(user).canRead() || user.isRoot()
                || user.equals(getOwner());
    }

    public boolean hasWritePermission(User user) {
        return findPermissionsForUser(user) != null && findPermissionsForUser(user).canWrite() || user.isRoot()
                || user.equals(getOwner());
    }
}
