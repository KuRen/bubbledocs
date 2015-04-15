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
}
