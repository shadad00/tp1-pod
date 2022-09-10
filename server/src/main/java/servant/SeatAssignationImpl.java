package servant;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;
import ar.edu.itba.models.Ticket;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SeatAssignationImpl implements SeatAssignation {

    private FlightCentral flightCentral;

    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }

    @Override
    public boolean isSeatFree(String flightCode, Integer row, Integer col) throws RemoteException {
        return flightCentral.getFlight(flightCode).isSeatAvailable(row, col);
    }

    @Override
    public void assignSeat(String flightCode, String passenger, Integer row, Integer col) throws RemoteException {
        flightCentral.getFlight(flightCode).assignSeat(passenger, row, col);
    }

    @Override
    public void movePassenger(String flightCode, String passenger, Integer row, Integer col) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        flight.freePassengerSeat(passenger);
        assignSeat(flightCode, passenger, row, col);
    }

    @Override
    public List<Flight> checkAlternativeFlights(Flight flight, Ticket ticket) throws RemoteException {
        return flightCentral.getAlternativeFlights( ticket.getCategory(), flight.getDestiny()).stream().sorted(
                Comparator.comparing((Flight f) -> f.getBestAvailableCategory(ticket.getCategory()))
                        .thenComparingInt(Flight::getAvailableSeats).reversed()
                        .thenComparing(Flight::getFlightCode)
        ).collect(Collectors.toList());

    }

    @Override
    public void changeTicket(String passenger, String original, String alternative) throws RemoteException {
        Flight originalFlight = Optional.ofNullable(flightCentral.getFlight(original)).orElseThrow(RemoteException::new);
        Flight alternativeFlight = Optional.ofNullable(flightCentral.getFlight(alternative)).orElseThrow(RemoteException::new);
        if(!originalFlight.passengerExists(passenger))
            throw new RemoteException();
        Ticket ticket = originalFlight.deletePassenger(passenger);
        alternativeFlight.addTicket(ticket);
    }
}
