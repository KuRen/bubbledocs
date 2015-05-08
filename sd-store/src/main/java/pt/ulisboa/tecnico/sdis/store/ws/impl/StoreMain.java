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
        int nServers = 2;
        int write = 1;
        int read = 1;
        
        List<StoreServer> servers = new ArrayList<StoreServer>();
        
        for(int i = 0; i<nServers; i++) {
            servers.add(new StoreServer(uddiURL, name + i, url.replace("8080", "890" + i)));
        }
        
        for(StoreServer server : servers) {
            server.run();
            System.out.println("Server Started!");
        }
        
        SDStore frontend = new FrontEnd(uddiURL, name, nServers, write, read);
        
        Endpoint endpoint = Endpoint.create(frontend);
        endpoint.publish(url);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);
        
        // wait
        System.out.println("Awaiting connections");
        System.out.println("Press enter to shutdown");
        System.in.read();
        DocUserPair x = new DocUserPair();
        x.setDocumentId("a1");
        x.setUserId("alice");
        byte[] y = frontend.load(x);
        System.out.println(new String(y));
        frontend.store(x, "nova-versao".getBytes());
        y = frontend.load(x);
        System.out.println(new String(y));
        
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
