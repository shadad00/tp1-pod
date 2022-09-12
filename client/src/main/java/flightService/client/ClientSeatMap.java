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

            if (category != null) {
                service.getSeatMapByCategory(clientArguments.getFlightCode(), SeatCategory.valueOf(category));
            }

            String row = clientArguments.getRow();

            if (row != null) {
                service.getSeatMapByRow(clientArguments.getFlightCode(), Integer.parseInt(row));
            }


            System.out.println(service.getSeatMap(clientArguments.getFlightCode()));

        } catch (NotBoundException | RemoteException | MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
