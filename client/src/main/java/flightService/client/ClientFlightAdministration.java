package flightService.client;

import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.models.Ticket;
import ar.edu.itba.models.utils.RowColumnPair;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.client.arguments.ArgumentsFlightAdministration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientFlightAdministration {

    public static void main(String[] args) {
        try {
            ArgumentsFlightAdministration clientArguments = new ArgumentsFlightAdministration();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

//            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());
            final FlightAdministration service = (FlightAdministration) Naming.lookup("//" + clientArguments.getAddress() + "/" + FlightAdministration.class.getName());
//            final FlightAdministration service = (FlightAdministration) registry.lookup("123");

            String action = clientArguments.getAction();

            switch (action) {
                case "models":
                    List<String> models = Files.readAllLines(Paths.get(clientArguments.getInPath()) );
                    models.remove(0); //Ignores header of CSV file
                    parseModelsFile(models, service);
                    break;
                case "flights":
                    List<String> flights = Files.readAllLines(Paths.get(clientArguments.getInPath()) );
                    flights.remove(0); //Ignores header of CSV file
                    parseFlightsFile(flights, service);
                    break;
                case "status":
                    System.out.println("Flight " + clientArguments.getFlightCode()  + " is " +
                            service.getFlightStatus(clientArguments.getFlightCode()));
                    break;
                case "confirm":
                    service.confirmFlight(clientArguments.getFlightCode());
                    System.out.println("Flight" + clientArguments.getFlightCode()  + "is confirmed");
                    break;
                case "cancel":
                    service.cancelFlight(clientArguments.getFlightCode());
                    System.out.println("Flight" + clientArguments.getFlightCode()  + "is cancelled");
                    break;
                case "retticketing":
                    service.forceTicketChangeForCancelledFlights();
                    break;
            }

        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        } catch (MalformedURLException me) {
            System.out.println("ERROR: Malformed URL");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Boeing 787;BUSINESS#2#3,PREMIUM_ECONOMY#3#3,ECONOMY#20#10
    private static void parseModelsFile(List<String> file, FlightAdministration service) throws RemoteException {
        for(String line : file ) {
            String[] parse = line.split(";");
            EnumMap<SeatCategory, RowColumnPair> seats = parseModels(parse[1]);
            service.addPlaneModel(parse[0], seats);

        }

        System.out.println(file.size() + " models registered\n");
    }

    private static EnumMap<SeatCategory, RowColumnPair> parseModels(String modelLines) {
        EnumMap<SeatCategory, RowColumnPair> seatsCategory = new EnumMap<>(SeatCategory.class);
        for (String s : modelLines.split(",")) {
            String[] categoryEntry = s.split("#");
            seatsCategory.put(SeatCategory.valueOf(categoryEntry[0]),new RowColumnPair(
                    Integer.parseInt(categoryEntry[1]), Integer.parseInt(categoryEntry[2])));
        }
        return seatsCategory;
    }

    //Boeing 787;AA100;JFK;BUSINESS#John,ECONOMY#Juliet,BUSINESS#Elizabeth
    private static void parseFlightsFile(List<String> file, FlightAdministration service) throws RemoteException {
        for(String line : file ) {
            String[] parse = line.split(";");
            Map<String, Ticket> passengers = parseTickets(parse[3]);
            service.addFlight(parse[0], parse[1], parse[2], passengers);
        }

        System.out.println(file.size() + " Flights created\n");
    }

    private static Map<String, Ticket> parseTickets(String modelLines) {
        Map<String, Ticket> tickets = new HashMap<>();
        for (String s : modelLines.split(",")) {
            String[] categoryEntry = s.split("#");
            tickets.put(categoryEntry[1],
                    new Ticket(categoryEntry[1],SeatCategory.valueOf(categoryEntry[0])));
        }
        return tickets;
    }
}
