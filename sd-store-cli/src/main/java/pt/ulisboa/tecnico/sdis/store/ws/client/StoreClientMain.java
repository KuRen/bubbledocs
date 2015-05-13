package pt.ulisboa.tecnico.sdis.store.ws.client;

import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;

public class StoreClientMain {

    private static final String ticket =
            "lOmEnmVhotdovGUQOrWBwgBtKs9sPshTvbqfpQx9cb5ErnwSn8oSd7BhR5YMo+YBSCsi6v5mp8s8C1ZcSLtH6D8J+jlHhflBqy3flnn0Ph2C7/3hBxxIyCHPoQJQgdoc6BHHJvPZz2TdFL+M650Aq/vzKPKdv5FlV3LpS/QegBWnEqbv0ZMSYuDOc/T2gwDN+/1Ss8C+xMCSXj8fXCd9Is2cpkuIeXrC7FYDjg6pY7VYo3naEkxYuygcOBqDie1RyKExk0LUfzl63aUHGJxwyjjSPQIJ6G05E+OdJUW+2j8qy+PwfaMdRnm/epKAe7LEepx41MYm9o675fa99yrJXYlKuPo35GvexlD7/Hj0ODs=";

    private static final String key = "Om4TPaXmjVFdirX9Mvxxrg==";

    public static void main(String[] args) throws Throwable {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", StoreClientMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];
        int nReplicas = 5;
        int writeThreshold = 3;
        int readThreshold = 3;

        FrontEnd frontend = new FrontEnd(uddiURL, name, nReplicas, writeThreshold, readThreshold);

        System.out.println("Alright, let's work this out...");
        System.out.println("For this demonstration, we're going to use Alice's file \"a1\".");
        System.out.println("To start, press enter.");
        System.in.read();

        DocUserPair docUserPair = new DocUserPair();
        docUserPair.setDocumentId("a1");
        docUserPair.setUserId("alice");

        System.out.println("What's in Alice's file? Let's print its content and find out!");
        byte[] contents = frontend.load(docUserPair, ticket, key);
        System.out.println(new String(contents));

        System.out.println("Nice! But it could be a little better... Let's change it for another thing.");
        frontend.store(docUserPair, "Alice rocks! Kappa 123".getBytes());

        System.out.println("Let's check if the content changed!");
        contents = frontend.load(docUserPair);
        System.out.println(new String(contents));

        frontend.store(docUserPair, "Other thing just to see what happens...".getBytes());
        System.out.println(new String(frontend.load(docUserPair)));

        System.out.println("Awesome! Good job.");
    }
}
