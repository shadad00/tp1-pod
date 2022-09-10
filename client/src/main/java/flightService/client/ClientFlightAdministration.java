package flightService.client;

import ar.edu.itba.remoteInterfaces.FlightAdministration;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import com.sun.javaws.exceptions.InvalidArgumentException;
import flightService.client.arguments.ArgumentsFlightAdministration;
import flightService.client.arguments.ArgumentsSeatAssignation;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientFlightAdministration {

    public static void main(String[] args) {
        try {
            ArgumentsFlightAdministration clientArguments = new ArgumentsFlightAdministration();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (InvalidArgumentException e) {
                System.out.println(e.getMessage());
            }

            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());

            final SeatAssignation service = (SeatAssignation) registry.lookup(FlightAdministration.class.getName());

            String action = clientArguments.getAction();

            if (action.equals("seatAssign")){
                servi
            }

        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me) {
            System.out.println("ERROR: Malformed URL");
        }
    }
}
