package flightService.client;

import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.client.arguments.ArgumentsSeatAssignation;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientSeatAssignation {

    public static void main(String[] args) {
        try {
            ArgumentsSeatAssignation clientArguments = new ArgumentsSeatAssignation();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

//            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());
//            final SeatAssignation service = (SeatAssignation) registry.lookup(SeatAssignation.class.getName());
            final SeatAssignation service = (SeatAssignation) Naming.lookup("//" + clientArguments.getAddress() + "/" + SeatAssignation.class.getName());

            String action = clientArguments.getAction();

            switch (action) {
                case "assign":
                    service.assignSeat(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
                    break;
                case "status":
                    String passenger;
                    if ( (passenger=service.isSeatFree(clientArguments.getFlightCode(), clientArguments.getRow(), clientArguments.getCol())) == null)
                        System.out.printf("seat %d%c is free\n%n",clientArguments.getRow(),clientArguments.getCol() );
                    else
                        System.out.printf("seat %d%c is assigned to: %s\n%n",clientArguments.getRow(),clientArguments.getCol() ,passenger);
                    break;
                case "move":
                    service.movePassenger(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
                    break;
                case "alternatives":
                    System.out.println(service.checkAlternativeFlights(clientArguments.getFlightCode(), clientArguments.getPassenger()));
                    break;
                case "changeTicket":
                    service.changeTicket(clientArguments.getPassenger(), clientArguments.getOriginalFlight(), clientArguments.getFlightCode());
                    break;
            }

        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
//        catch (MalformedURLException me) {
//            System.out.println("ERROR: Malformed URL");
//        }
    }
}
