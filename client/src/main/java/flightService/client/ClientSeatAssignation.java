package flightService.client;

import ar.edu.itba.remoteInterfaces.SeatAssignation;
import com.sun.javaws.exceptions.InvalidArgumentException;
import flightService.client.arguments.ArgumentsSeatAssignation;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientSeatAssignation {

    public static void main(String[] args) {
        try {
            ArgumentsSeatAssignation clientArguments = new ArgumentsSeatAssignation();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (InvalidArgumentException e) {
                System.out.println(e.getMessage());
            }

            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());

            final SeatAssignation service = (SeatAssignation) registry.lookup(SeatAssignation.class.getName());

            String action = clientArguments.getAction();

            if (action.equals("assign")){
                service.assignSeat(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
            }
            else if(action.equals("status")){
                service.isSeatFree(clientArguments.getFlightCode(), clientArguments.getRow(), clientArguments.getCol());
            }
            else if(action.equals("move")){
                service.movePassenger(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
            }
            else if(action.equals("alternatives")){
                service.checkAlternativeFlights(clientArguments.getFlightCode(), clientArguments.getPassenger());
            }
            else if(action.equals("changeTicket")){
                service.changeTicket(clientArguments.getPassenger(), clientArguments.getFlightCode(),clientArguments.getOriginalFlight());
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
