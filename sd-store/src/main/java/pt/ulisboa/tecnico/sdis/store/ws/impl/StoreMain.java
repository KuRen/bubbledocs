package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;

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
        int nReplicas = 5;

        List<StoreServer> listOfReplicas = new ArrayList<StoreServer>();

        for (int i = 0; i < nReplicas; i++) {
            listOfReplicas.add(new StoreServer(uddiURL, name + i, url.replace("8080", "890" + i)));
        }

        for (StoreServer replica : listOfReplicas) {
            replica.run();
            System.out.println("Server Started!");
        }

        System.out.println("Press enter to stop.");
        System.in.read();

        for (StoreServer replica : listOfReplicas)
            replica.stop();
    }
}