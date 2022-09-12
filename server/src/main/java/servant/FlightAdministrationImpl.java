package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.RowColumnPair;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class FlightAdministrationImpl implements FlightAdministration {

    private final FlightCentral flightCentral;

    public FlightAdministrationImpl(FlightCentral flightCentral){
        this.flightCentral=flightCentral;
    }

    @Override
    public void addPlaneModel(String model, EnumMap<SeatCategory, RowColumnPair> categories) throws RemoteException {

        if(flightCentral.getModels(model) != null) {
            System.out.println("Model already exists");
            throw new IllegalArgumentException();
        }

        EnumMap<SeatCategory, CategoryDescription> categoryDescriptors = new EnumMap<>(SeatCategory.class);

        int totalRows = 0;
        for (SeatCategory category : SeatCategory.values()
             ) {
            RowColumnPair pair = categories.get(category);
            if(pair != null && pair.getRow() > 0 && pair.getColumn() > 0) {
                categoryDescriptors.put(category, new CategoryDescription(category, totalRows+1, totalRows + pair.getRow(), pair.getColumn()));
                totalRows += pair.getRow();
            }
        }

        if(totalRows <= 0) {
            System.out.println("Invalid rows and columns");
            throw new IllegalArgumentException();
        }

        flightCentral.addModel(model, new Plane(model, categoryDescriptors));

    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, Map<String, Ticket> tickets) throws RemoteException {
        if(!flightCentral.modelExists(modelName)
                || flightCentral.flightExists(flightCode) ){
            System.out.println("model or flight already exists " + modelName + " " + flightCode);
            throw new IllegalArgumentException();
        }

        flightCentral.addFlight(flightCode, new Flight(flightCode, destinationAirportCode, flightCentral.getModels(modelName), tickets));

    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return Optional.ofNullable(flightCentral.getFlight(flightCode))
                .orElseThrow(IllegalArgumentException::new).getFlightStatus();
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
            throw new IllegalArgumentException();
        flight.setStatus(FlightStatus.CONFIRMED);
        flightCentral.notifyConfirmation(flight);
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
            throw new IllegalArgumentException();
        flight.setStatus(FlightStatus.CANCELLED);
        flightCentral.notifyCancellation(flight);
    }

    @Override
    public void forceTicketChangeForCancelledFlights() {
        flightCentral.getFlights().values().stream().filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED))
                .sorted(Comparator.comparing(Flight::getFlightCode))
                //for each cancelled flight ordered by flightCode.
                .forEach(
                oldFlight -> oldFlight.getTickets().stream().sorted(Comparator.comparing(Ticket::getPassenger))
                        //For each passenger in the flight order by Passenger's name.
                        .forEach(
                        ticket -> {

                            List<Flight> alternatives = getAlternatives(ticket, oldFlight.getDestiny(),oldFlight.getFlightCode());

                            if(!alternatives.isEmpty()){
                                Optional<Flight> maybeNewFlight = alternatives.stream()
                                        .min(Comparator.comparing((Flight f) -> f.getBestAvailableCategory(ticket.getCategory()))
                                        .thenComparingInt(Flight::getAvailableSeats).reversed()
                                        .thenComparing(Flight::getFlightCode));
                                maybeNewFlight.ifPresent(newFlight -> {
                                    ticket.clearSeat();
                                    newFlight.addTicket(ticket);
                                    //todo: remove ticket from oldFlight ?
                                    //todo: notify service ?
                                });
                            }

                        }
                )
        );

    }

    private List<Flight> getAlternatives(Ticket ticket, String destiny,String selfFlightCode) {
        return flightCentral.getAlternativeFlights(ticket.getCategory(),destiny,selfFlightCode);
    }
}
