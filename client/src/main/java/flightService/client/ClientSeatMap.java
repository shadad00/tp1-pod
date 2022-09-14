package flightService.client;

import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.remoteInterfaces.FlightNotification;
import ar.edu.itba.remoteInterfaces.SeatMap;
import flightService.client.arguments.ArgumentsSeatMap;

import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientSeatMap {
    public static void main(String[] args) {
        try {
            ArgumentsSeatMap clientArguments = new ArgumentsSeatMap();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

//            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());
//
//            final SeatMap service = (SeatMap) registry.lookup(SeatMap.class.getName());
            final SeatMap service = (SeatMap) Naming.lookup("//" + clientArguments.getAddress() + "/" + SeatMap.class.getName());

            String category = clientArguments.getCategory();
            String row = clientArguments.getRow();
            String output;
            if (category != null) {
                 output = service.getSeatMapByCategory(clientArguments.getFlightCode(), SeatCategory.valueOf(category));
            }
            else if (row != null) {
                output = service.getSeatMapByRow(clientArguments.getFlightCode(), Integer.parseInt(row));
            }
            else {
                output = service.getSeatMap(clientArguments.getFlightCode());
            }


            System.out.println(output);

        } catch (NotBoundException | RemoteException | IllegalArgumentException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }
}
