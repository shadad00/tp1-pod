package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.RowColumnPair;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlightAdministrationImpl implements FlightAdministration {

    private FlightCentral flightCentral;

    @Override
    public void addPlaneModel(String model, EnumMap<SeatCategory, RowColumnPair> categories) throws RemoteException {

        EnumMap<SeatCategory, CategoryDescription> categoryDescriptors = new EnumMap<>(SeatCategory.class);

        int totalRows = 0;
        for (SeatCategory category : SeatCategory.values()
             ) {
            RowColumnPair pair = categories.get(category);
            if(pair.getRow() > 0 && pair.getColumn() > 0) {
                categoryDescriptors.put(category, new CategoryDescription(totalRows+1, totalRows + pair.getRow(), pair.getColumn()));
                totalRows += pair.getRow();
            }
        }

        if(totalRows <= 0)
            throw new RemoteException();        //// TODO add custom exception

        flightCentral.addModel(model, new Plane(model, categoryDescriptors));

    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, List<Ticket> tickets) throws RemoteException {
        if(!flightCentral.modelExists(modelName) || flightCentral.flightExists(flightCode))
            throw new RemoteException();

        flightCentral.addFlight(flightCode, new Flight(flightCode, destinationAirportCode, flightCentral.getModels(modelName), tickets));

    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new).getStatus();
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new).setStatus(FlightStatus.CONFIRMED);
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new).setStatus(FlightStatus.CANCELLED);
    }

    @Override
    public void forceTicketChangeForCancelledFlights() {
        flightCentral.getFlights().values().stream().filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED))
                .forEach(
                flight -> flight.getTickets().stream().sorted(Comparator.comparing(Ticket::getName)).forEach(
                        ticket -> {

                            List<Flight> alternatives = getAlternatives(ticket, flight.getDestiny());

                            if(!alternatives.isEmpty()){
                                Optional<Flight> newFlight = alternatives.stream().min(Comparator.comparing((Flight f) -> f.getBestAvailableCategory(ticket.getCategory()))
                                        .thenComparingInt(Flight::getAvailableSeats).reversed()
                                        .thenComparing(Flight::getFlightCode));
                                newFlight.ifPresent(value -> value.addTicket(ticket));
                            }

                        }
                )
        );

    }

    private List<Flight> getAlternatives(Ticket ticket, String destiny) {
        return flightCentral.getFlights().values().stream().filter(flight -> flight.getDestiny().equals(destiny)).filter(flight -> flight.hasAvailableSeats(ticket.getCategory())).collect(Collectors.toList());
    }
}
