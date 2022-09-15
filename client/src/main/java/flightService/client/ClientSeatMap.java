package flightService.client;

import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.remoteInterfaces.SeatMap;
import flightService.client.arguments.ArgumentsSeatMap;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientSeatMap {
    public static void main(String[] args) {
        try {
            ArgumentsSeatMap clientArguments = new ArgumentsSeatMap();

            clientArguments.parseArguments();

            final SeatMap service = (SeatMap) Naming.lookup("//" + clientArguments.getAddress() + "/" + SeatMap.class.getName());

            String category = clientArguments.getCategory();
            Integer row = clientArguments.getRow();
            String output;
            if (category != null) {
                 output = service.getSeatMapByCategory(clientArguments.getFlightCode(), SeatCategory.valueOf(category));
            }
            else if (row != null) {
                output = service.getSeatMapByRow(clientArguments.getFlightCode(), row);
            }
            else {
                output = service.getSeatMap(clientArguments.getFlightCode());
            }

            try {
                FileWriter myObj = new FileWriter(clientArguments.getOutPath());
                myObj.write(output);
                myObj.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
            }

        } catch (NotBoundException | RemoteException | IllegalArgumentException | MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }
}
