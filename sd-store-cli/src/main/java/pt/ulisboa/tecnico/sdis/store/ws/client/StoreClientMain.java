package pt.ulisboa.tecnico.sdis.store.ws.client;

import java.util.ArrayList;

import pt.ulisboa.tecnico.sdis.store.ws.client.StoreClient;
import pt.ulisboa.tecnico.sdis.store.ws.DocDoesNotExist_Exception;
import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;

public class StoreClientMain {

    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", StoreClientMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];

        StoreClient client = new StoreClient(uddiURL, name);

        DocUserPair pair = new DocUserPair();
        pair.setDocumentId("fail");
        pair.setUserId("carla");

        if (!client.listDocs("carla").equals(new ArrayList<String>()))
            System.out.println("Error: carla docs should be null!");
        try {
            client.store(pair, new String("fail").getBytes());
            System.out.println("Error: carla doc shouldnt exist!");
        } catch (DocDoesNotExist_Exception e) {
        }
        try {
            client.load(pair);
            System.out.println("Error: carla doc shouldnt exist!");
        } catch (DocDoesNotExist_Exception e) {
        }
    }
}
