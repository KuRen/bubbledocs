package pt.tecnico.bubbledocs.domain;

public class Root extends Root_Base {

    public Root() {
        super();
        super.init("root", "root", "root@root.root", "Super User");
    }

    public static User getInstance() {
        BubbleDocs bd = BubbleDocs.getInstance();

        for (User u : bd.getUsersSet()) {
            if (u.getUsername().equals("root"))
                return u;
        }

        User root = new Root();
        bd.addUsers(root);

        return root;
    }

    @Override
    public boolean isRoot() {
        return true;
    }

}
