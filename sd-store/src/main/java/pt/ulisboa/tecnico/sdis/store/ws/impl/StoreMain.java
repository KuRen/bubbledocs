package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.Endpoint;

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
        
        List<StoreServer> servers = new ArrayList<StoreServer>();
        
        for(int i = 0; i<5; i++) {
            servers.add(new StoreServer(uddiURL, name + i, url.replace("8080", "890" + i)));
        }
        
        for(StoreServer server : servers) {
            server.run();
            System.out.println("Server Started!");
        }
        
        Endpoint endpoint = Endpoint.create(new FrontEnd(uddiURL, name));
        endpoint.publish(url);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);
        
        
        
        // wait
        System.out.println("Awaiting connections");
        System.out.println("Press enter to shutdown");
        System.in.read();
        for(StoreServer server : servers)
            server.stop();
        try {
            if (endpoint != null)
                endpoint.stop();
        } catch(Exception e) {
            System.out.printf("Caught exception when stopping: %s%n", e);
        }
        try {
            if (uddiNaming != null)
                uddiNaming.unbind(name);
        } catch(Exception e) {
            System.out.printf("Caught exception when deleting: %s%n", e);
        }
    }
}
