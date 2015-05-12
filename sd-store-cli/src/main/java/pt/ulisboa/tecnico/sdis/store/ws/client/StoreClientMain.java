package pt.ulisboa.tecnico.sdis.store.ws.client;

import pt.ulisboa.tecnico.sdis.store.ws.DocUserPair;

public class StoreClientMain {

    private static final String ticket =
            "rqKyXJCTyu0a8xeIkks9CaUxsMpD/yd8Y8xiXOewmsUhxyk5Em1j3wRxZ39UnAPpgwjoNYVpQuSi3VEq+Jt1i6PCRQVuho9Ze5Viorp4/Xy5KnYAUB2Xb+3/xAM0FWGUSG3oFFnQAl9Duk7MW8bo6G+ROUtJvWYWqeMfcN5k+mW70fjNdK+AtpJdNQaexCUg6MC4yeyPcvr9b1rwMcxum3pPrFLh+tvBGx8/4fR69tt65aYmIPUyfDoH0zjdd2ujH4iIQGncICel1PzQZnx5e5sd7GQqRzF1MiYL/i3tx9jVlo2xQl7qi7/HkkLPehQBbtdB4BD6gKaERlmv0bpN0vUw38S8EnNl";

    private static final String key = "SomeKeyAplha42";

    public static void main(String[] args) throws Exception {
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
