package pt.ulisboa.tecnico.sdis.id.client;

import pt.ulisboa.tecnico.sdis.id.ws.AuthReqFailed_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.EmailAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidEmail_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.InvalidUser_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserAlreadyExists_Exception;
import pt.ulisboa.tecnico.sdis.id.ws.UserDoesNotExist_Exception;

public class IdClientMain {

    public static void main(String[] args) {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL name%n", IdClientMain.class.getName());
            return;
        }

        String uddiURL = args[0];
        String serviceName = args[1];

        IdClient client = null;
        try {
            client = new IdClient(uddiURL, serviceName);
            client.setVerbose(true);
            //client.lookForService(uddiURL, serviceName);
            //client.createStub();
        } catch (serviceFindException e) {
            System.out.println("Could not connect to server!");
            e.printStackTrace();
        }

        try {
            client.createUser("Harry", "master_potter@dmle.ministryofmagic.uk");
            System.out.println("Created user: Harry, master_potter@defense.ministryofmagic.uk");
        } catch (EmailAlreadyExists_Exception | InvalidEmail_Exception | InvalidUser_Exception | UserAlreadyExists_Exception e) {
            System.out.println("Error creating user: Harry, master_potter@defense.ministryofmagic.uk");
        }

        try {
            byte[] success = client.requestAuthentication("alice", "Aaa1".getBytes());
            if (success[0] == 1)
                System.out.println("Successful login with user: alice");
            else
                System.out.println("Error in login with user: alice");
        } catch (AuthReqFailed_Exception e) {
            System.out.println("Failed login with user: alice");
        }

        try {
            client.removeUser("Harry");
            System.out.println("Deleted Harry, it does not exist anymore");
        } catch (UserDoesNotExist_Exception e) {
            System.out.println("Could not delete Harry, it does not exist");
        }

        try {
            client.createUser("Hermione", "hgranger@dmle.ministryofmagic.uk");
            System.out.println("Created user: Hermione, hgrangerr@dmle.ministryofmagic.uk");
        } catch (EmailAlreadyExists_Exception | InvalidEmail_Exception | InvalidUser_Exception | UserAlreadyExists_Exception e) {
            System.out.println("Error creating user: Hermione, hgranger@dmle.ministryofmagic.uk");
        }

        try {
            client.renewPassword("Hermione");
            System.out.println("Renewed password of user Hermione");
        } catch (UserDoesNotExist_Exception e) {
            System.out.println("Error renewing password of user Hermione");
        }

        try {
            client.removeUser("Hermione");
            System.out.println("Deleted Hermione, it does not exist anymore");
        } catch (UserDoesNotExist_Exception e) {
            System.out.println("Could not delete Hermione, it does not exist");
        }

    }

}
