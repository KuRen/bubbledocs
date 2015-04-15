package pt.ulisboa.tecnico.sdis.id.client;

import javax.xml.registry.JAXRException;

public class IdClientMain {

    public static void main(String[] args) throws IdClientException, JAXRException {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", IdClientMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String serviceName = args[1];

        IdClient client = new IdClient(uddiURL, serviceName);

    }

}
