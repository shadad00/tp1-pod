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
            clientArguments.parseArguments();

            final SeatAssignation service = (SeatAssignation) Naming.lookup("//" + clientArguments.getAddress() + "/" + SeatAssignation.class.getName());

            String action = clientArguments.getAction();

            switch (action) {
                case "assign":
                    try {
                        service.assignSeat(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
                    }catch (IllegalArgumentException | IllegalStateException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "status":
                    String passenger;
                    try {
                         passenger=service.isSeatFree(clientArguments.getFlightCode(), clientArguments.getRow(), clientArguments.getCol());
                        if ( passenger == null)
                            System.out.printf("seat %d%c is FREE\n%n",clientArguments.getRow(),clientArguments.getCol() );
                        else
                            System.out.printf("seat %d%c is ASSIGNED to: %s\n%n",clientArguments.getRow(),clientArguments.getCol() ,passenger);
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "move":
                    try {
                        service.movePassenger(clientArguments.getFlightCode(), clientArguments.getPassenger(), clientArguments.getRow(), clientArguments.getCol());
                    }catch (IllegalArgumentException | IllegalStateException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "alternatives":
                    try {
                        System.out.println(service.checkAlternativeFlights(clientArguments.getFlightCode(), clientArguments.getPassenger()));
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "changeTicket":
                    try {
                        service.changeTicket(clientArguments.getPassenger(), clientArguments.getOriginalFlight(), clientArguments.getFlightCode());
                    }catch (IllegalArgumentException | IllegalStateException e){
                        System.out.println(e.getMessage());
                    }
                    break;
            }

        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }catch (IllegalArgumentException | IllegalStateException e){
            System.out.println(e.getMessage());
        }

    }
}
