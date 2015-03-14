package pt.tecnico.bubbledocs;

public class BubbleApplication {

    public static void main(String[] args) {
        System.out.println("//Started Bubble App.");
        setupDomain();
    }

    private static void setupDomain() {
        SetupDomain.populateDomain();
        System.out.println("//Finished populating domain");
    }
}