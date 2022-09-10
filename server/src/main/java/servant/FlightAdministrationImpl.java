package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.RowColumnPair;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;

public class FlightAdministrationImpl implements FlightAdministration {

    private FlightCentral flightCentral;

    public FlightAdministrationImpl(FlightCentral flightCentral){
        this.flightCentral=flightCentral;
    }

    @Override
    public void addPlaneModel(String model, EnumMap<SeatCategory, RowColumnPair> categories) throws RemoteException {

        EnumMap<SeatCategory, CategoryDescription> categoryDescriptors = new EnumMap<>(SeatCategory.class);

        int totalRows = 0;
        for (SeatCategory category : SeatCategory.values()
             ) {
            RowColumnPair pair = categories.get(category);
            if(pair.getRow() > 0 && pair.getColumn() > 0) {
                categoryDescriptors.put(category, new CategoryDescription(category, totalRows+1, totalRows + pair.getRow(), pair.getColumn()));
                totalRows += pair.getRow();
            }
        }

        if(totalRows <= 0)
            throw new RemoteException();        //// TODO add custom exception

        flightCentral.addModel(model, new Plane(model, categoryDescriptors));

    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, Map<String, Ticket> tickets) throws RemoteException {
        if(!flightCentral.modelExists(modelName) || flightCentral.flightExists(flightCode))
            throw new RemoteException();

        flightCentral.addFlight(flightCode, new Flight(flightCode, destinationAirportCode, flightCentral.getModels(modelName), tickets));

    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
            throw new RemoteException();
        return Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new).getStatus();
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
            throw new RemoteException();
        flight.setStatus(FlightStatus.CONFIRMED);
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
            throw new RemoteException();
        flight.setStatus(FlightStatus.CANCELLED);
    }

    @Override
    public void forceTicketChangeForCancelledFlights() {
        flightCentral.getFlights().values().stream().filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED)).sorted(Comparator.comparing(Flight::getFlightCode))
                .forEach(
                flight -> flight.getTickets().stream().sorted(Comparator.comparing(Ticket::getPassenger)).forEach(
                        ticket -> {

                            List<Flight> alternatives = getAlternatives(ticket, flight.getDestiny());

                            if(!alternatives.isEmpty()){
                                Optional<Flight> newFlight = alternatives.stream().min(Comparator.comparing((Flight f) -> f.getBestAvailableCategory(ticket.getCategory()))
                                        .thenComparingInt(Flight::getAvailableSeats).reversed()
                                        .thenComparing(Flight::getFlightCode));
                                newFlight.ifPresent(value -> {
                                    value.addTicket(ticket);
                                    ticket.clearSeat();
                                });
                            }

                        }
                )
        );

    }

    private List<Flight> getAlternatives(Ticket ticket, String destiny) {
        return flightCentral.getAlternativeFlights(ticket.getCategory(),destiny);
    }
}
