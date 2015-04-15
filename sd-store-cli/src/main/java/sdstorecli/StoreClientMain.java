package sdstorecli;

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

        StoreClient client = new StoreClient(uddiURL,name);

        DocUserPair pair = new DocUserPair();
        pair.setDocumentId("HelloDoc");
        pair.setUserId("MrHello");
        System.out.println("Creating file HelloDoc by MrHello ...");
        client.createDoc(pair);
        System.out.println("Storing string Hello in file HelloDoc by MrHello ...");
        client.store(pair, new String("Hello").getBytes());
        System.out.println("Loading file HelloDoc by MrHello ...");
        byte[] result = client.load(pair);
        System.out.println("File Content: " + new String(result));
    }

}
