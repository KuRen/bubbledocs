package pt.ulisboa.tecnico.sdis.id.ws.impl;

import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.impl.uddi.UDDINaming;

public class IdMain {

    public static void main(String[] args) {
        // Check arguments
        if (args.length < 3) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL wsName wsURL%n", IdMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String name = args[1];
        String url = args[2];

        Endpoint endpoint = null;
        UDDINaming uddiNaming = null;
        try {
            populate();

            endpoint = Endpoint.create(new IdImpl());

            // publish endpoint
            System.out.printf("Starting %s%n", url);
            endpoint.publish(url);

            // publish to UDDI
            System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
            uddiNaming = new UDDINaming(uddiURL);
            uddiNaming.rebind(name, url);

            // wait
            System.out.println("Awaiting connections");
            System.out.println("Press enter to shutdown");
            System.in.read();

        } catch (Exception e) {
            System.out.printf("Caught exception: %s%n", e);
            e.printStackTrace();

        } finally {
            try {
                if (endpoint != null) {
                    // stop endpoint
                    endpoint.stop();
                    System.out.printf("Stopped %s%n", url);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when stopping: %s%n", e);
            }
            try {
                if (uddiNaming != null) {
                    // delete from UDDI
                    uddiNaming.unbind(name);
                    System.out.printf("Deleted '%s' from UDDI%n", name);
                }
            } catch (Exception e) {
                System.out.printf("Caught exception when deleting: %s%n", e);
            }
        }

    }

    private static void populate() throws EmailAlreadyExists_Exception, InvalidEmail_Exception, InvalidUser_Exception,
            UserAlreadyExists_Exception {
        System.out.println("Populating initial state of SD-ID");
        UserManager userManager = UserManager.getInstance();

        userManager.create("alice", "alice@tecnico.pt", "Aaa1");
        System.out.printf("Added user <%s>, with email <%s> and password <%s>%n", "alice", "alice@tecnico.pt", "Aaa1");

        userManager.create("bruno", "bruno@tecnico.pt", "Bbb2");
        System.out.printf("Added user <%s>, with email <%s> and password <%s>%n", "bruno", "bruno@tecnico.pt", "Bbb2");

        userManager.create("carla", "carla@tecnico.pt", "Ccc3");
        System.out.printf("Added user <%s>, with email <%s> and password <%s>%n", "carla", "carla@tecnico.pt", "Ccc3");

        userManager.create("duarte", "duarte@tecnico.pt", "Ddd4");
        System.out.printf("Added user <%s>, with email <%s> and password <%s>%n", "duarte", "duarte@tecnico.pt", "Ddd4");

        userManager.create("eduardo", "eduardo@tecnico.pt", "Eee5");
        System.out.printf("Added user <%s>, with email <%s> and password <%s>%n", "eduardo", "eduardo@tecnico.pt", "Eee5");
    }

}
