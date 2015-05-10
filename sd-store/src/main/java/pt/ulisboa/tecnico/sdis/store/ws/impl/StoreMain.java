package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;
import pt.ulisboa.tecnico.sdis.store.ws.SDStore;
import pt.ulisboa.tecnico.sdis.store.ws.impl.uddi.UDDINaming;

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
        int writeThreshold = 3;
        int readThreshold = 3;

        List<StoreServer> listOfReplicas = new ArrayList<StoreServer>();

        for (int i = 0; i < nReplicas; i++) {
            listOfReplicas.add(new StoreServer(uddiURL, name + i, url.replace("8080", "890" + i)));
        }

        for (StoreServer replica : listOfReplicas) {
            replica.run();
            System.out.println("Server Started!");
        }

        SDStore frontend = new FrontEnd(uddiURL, name, nReplicas, writeThreshold, readThreshold);

        Endpoint endpoint = Endpoint.create(frontend);
        endpoint.publish(url);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);

        System.out.println("Alright, let's work this out...");
        System.out.println("For this demonstration, we're going to use Alice's file \"a1\".");
        System.out.println("To start, press enter.");
        System.in.read();

        DocUserPair docUserPair = new DocUserPair();
        docUserPair.setDocumentId("a1");
        docUserPair.setUserId("alice");

        System.out.println("What's in Alice's file? Let's print its content and find out!");
        byte[] contents = frontend.load(docUserPair);
        System.out.println(new String(contents));

        System.out.println("Nice! But it could be a little better... Let's change it for another thing.");
        frontend.store(docUserPair, "Alice rocks! Kappa 123".getBytes());

        System.out.println("Let's check if the content changed!");
        contents = frontend.load(docUserPair);
        System.out.println(new String(contents));

        frontend.store(docUserPair, "Other thing just to see what happens...".getBytes());
        System.out.println(new String(frontend.load(docUserPair)));

        System.out.println("Awesome! Good job.");

        for (StoreServer replica : listOfReplicas)
            replica.stop();

        try {
            if (endpoint != null)
                endpoint.stop();
        } catch (Exception e) {
            System.out.printf("Caught exception when stopping: %s%n", e);
        }

        try {
            if (uddiNaming != null)
                uddiNaming.unbind(name);
        } catch (Exception e) {
            System.out.printf("Caught exception when deleting: %s%n", e);
        }
    }
}