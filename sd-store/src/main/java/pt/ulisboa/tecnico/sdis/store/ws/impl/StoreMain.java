package pt.ulisboa.tecnico.sdis.store.ws.impl;

public class StoreMain {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL wsName wsURL%n", StoreMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];
        String url = args[2];
        
        StoreServer server = new StoreServer(uddiURL, name, url);
        populate();
        
        try {
        	server.run();
        } catch(Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();
        }
        
        // wait
        System.out.println("Awaiting connections");
        System.out.println("Press enter to shutdown");
        System.in.read();
        server.stop();
    }
    
    private static void populate() {
        System.out.println("-Populating initial state of SD-STORE-");
        UserManager userManager = UserManager.getInstance();

        userManager.createUser("alice");
        System.out.printf("Added user <%s>\n", "alice");

        userManager.createUser("bruno");
        System.out.printf("Added user <%s>\n", "bruno");

        userManager.createUser("carla");
        System.out.printf("Added user <%s>\n", "carla");

        userManager.createUser("duarte");
        System.out.printf("Added user <%s>\n", "duarte");

        userManager.createUser("eduardo");
        System.out.printf("Added user <%s>\n", "eduardo");
    }
}
