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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientFlightAdministration {

    public static void main(String[] args) {
        try {
            ArgumentsFlightAdministration clientArguments = new ArgumentsFlightAdministration();

            clientArguments.parseArguments();


            final FlightAdministration service = (FlightAdministration) Naming.lookup("//" + clientArguments.getAddress() + "/" + FlightAdministration.class.getName());

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
                    try {
                        System.out.println("Flight " + clientArguments.getFlightCode()  + " is " +
                                service.getFlightStatus(clientArguments.getFlightCode()));
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "confirm":
                    try {
                        service.confirmFlight(clientArguments.getFlightCode());
                        System.out.println("Flight " + clientArguments.getFlightCode()  + " is confirmed");
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                case "cancel":
                    try {
                        service.cancelFlight(clientArguments.getFlightCode());
                        System.out.println("Flight " + clientArguments.getFlightCode()  + " is cancelled");
                    }catch (IllegalArgumentException e){
                        System.out.println(e.getMessage());
                    }
                    break;
                case "retticketing":
                    System.out.println(service.forceTicketChangeForCancelledFlights());
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
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void parseModelsFile(List<String> file, FlightAdministration service) throws RemoteException {
        int modelsAdded = 0;
        for(String line : file ) {
            String[] parse = line.split(";");
            try {
                EnumMap<SeatCategory, RowColumnPair> seats = parseModels(parse[1], parse[0]);
                service.addPlaneModel(parse[0], seats);
                modelsAdded++;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        }

        System.out.println(modelsAdded + " models registered\n");
    }

    private static EnumMap<SeatCategory, RowColumnPair> parseModels(String modelLines, String model) {
        EnumMap<SeatCategory, RowColumnPair> seatsCategory = new EnumMap<>(SeatCategory.class);
        for (String s : modelLines.split(",")) {
            String[] categoryEntry = s.split("#");
            boolean added = false;
            for (SeatCategory value : SeatCategory.values()) {
                if (categoryEntry[0].equals(value.toString())) {
                    seatsCategory.put(SeatCategory.valueOf(categoryEntry[0]),new RowColumnPair(
                            Integer.parseInt(categoryEntry[1]), Integer.parseInt(categoryEntry[2])));
                    added=true;
                }
            }
            if (!added)
                throw new IllegalArgumentException("Cannot add model " + model);

        }
        return seatsCategory;
    }

    private static void parseFlightsFile(List<String> file, FlightAdministration service) throws RemoteException {
        int flightsAdded = 0;
        for(String line : file ) {
            String[] parse = line.split(";");
            try {
                ConcurrentHashMap<String, Ticket> passengers = parseTickets(parse[3], parse[0]);
                service.addFlight(parse[0], parse[1], parse[2], passengers);
                flightsAdded++;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println(flightsAdded + " Flights created\n");
    }

    private static ConcurrentHashMap<String, Ticket> parseTickets(String modelLines, String flightCode) {
        ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<>();
        for (String s : modelLines.split(",")) {
            String[] categoryEntry = s.split("#");
            boolean added = false;
            for (SeatCategory value : SeatCategory.values()) {
                if (categoryEntry[0].equals(value.toString())) {
                    tickets.put(categoryEntry[1],
                            new Ticket(categoryEntry[1],SeatCategory.valueOf(categoryEntry[0])));
                    added=true;
                }
            }
            if (!added)
                throw new IllegalArgumentException("Cannot add flight "+ flightCode);
        }
        return tickets;
    }
}
