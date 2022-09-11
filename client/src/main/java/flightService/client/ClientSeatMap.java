package flightService.client;

import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.remoteInterfaces.SeatMap;
import flightService.client.arguments.ArgumentsSeatMap;

import java.rmi.AccessException;
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

            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());

            final SeatMap service = (SeatMap) registry.lookup(SeatMap.class.getName());

            String category = clientArguments.getCategory();

            if (category != null) {
                service.getSeatMapByCategory(clientArguments.getFlightCode(), SeatCategory.valueOf(category));
            }

            String row = clientArguments.getRow();

            if (row != null) {
                service.getSeatMapByRow(clientArguments.getFlightCode(), Integer.parseInt(row));
            }


            service.getSeatMap(clientArguments.getFlightCode());

        } catch (AccessException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
