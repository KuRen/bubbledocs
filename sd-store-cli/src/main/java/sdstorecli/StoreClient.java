package sdstorecli;

import java.util.Map;

import javax.xml.ws.*;

import sdstorecli.uddi.UDDINaming;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import pt.ulisboa.tecnico.sdis.store.ws.*; // classes generated from WSDL


public class StoreClient {
	
    public static void main(String[] args) throws Exception {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", StoreClient.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];

        System.out.printf("Contacting UDDI at %s%n", uddiURL);
        UDDINaming uddiNaming = new UDDINaming(uddiURL);

        System.out.printf("Looking for '%s'%n", name);
        String endpointAddress = uddiNaming.lookup(name);

        if (endpointAddress == null) {
            System.out.println("Not found!");
            return;
        } else {
            System.out.printf("Found %s%n", endpointAddress);
        }

        System.out.println("Creating stub ...");
        SDStore_Service service = new SDStore_Service();
        SDStore port = service.getSDStoreImplPort();

        System.out.println("Setting endpoint address ...");
        BindingProvider bindingProvider = (BindingProvider) port;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);

        DocUserPair pair = new DocUserPair();
        pair.setDocumentId("HelloDoc");
        pair.setUserId("MrHello");
        System.out.println("Creating file HelloDoc by MrHello ...");
        port.createDoc(pair);
        System.out.println("Storing string Hello in file HelloDoc by MrHello ...");
        port.store(pair, new String("Hello").getBytes());
        System.out.println("Loading file HelloDoc by MrHello ...");
        byte[] result = port.load(pair);
        System.out.println("File Content: " + new String(result));
    }
    
}
