package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SeatAssignationImpl implements SeatAssignation {

    private final FlightCentral flightCentral;

    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }



    @Override
    public boolean isSeatFree(String flightCode, Integer row, Character col) throws RemoteException {
        return flightCentral.getFlight(flightCode).isSeatAvailable(row, fromColumnCharacter(col));
    }

    @Override
    public void assignSeat(String flightCode, String passenger, Integer row, Character col) throws RemoteException {
        assignSeatPrivate(flightCode,passenger,row, fromColumnCharacter(col) );
        flightCentral.notifyAssignation(passenger,flightCentral.getFlight(flightCode));
    }

    private boolean assignSeatPrivate(String flightCode, String passenger, Integer row, Integer col){
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode))
                .orElseThrow(IllegalArgumentException::new);
        if(!flight.getFlightStatus().equals(FlightStatus.PENDING))
            throw new IllegalArgumentException();
        Ticket passengerTicket = Optional.ofNullable(flight.getTicket(passenger))
                .orElseThrow(IllegalArgumentException::new);
        if(passengerTicket.hasSeat())
            throw new IllegalArgumentException();

        return flight.assignSeat(passenger, row, col);
    }


    @Override
    public void movePassenger(String flightCode, String passenger, Integer row, Character col) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);
        Ticket passengerTicket = flight.getTicket(passenger);
        Seat oldSeat = passengerTicket.getSeat();
        flight.freePassengerSeat(passenger);
        if( assignSeatPrivate(flightCode, passenger, row, fromColumnCharacter(col) ) )
            flightCentral.notifySeatChange(passenger,oldSeat,flight);
        else passengerTicket.assignSeat(oldSeat); //rollback
    }

    @Override
    public List<Flight> checkAlternativeFlights(String flightCode, String passenger) throws RemoteException {
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode))
                .orElseThrow(IllegalArgumentException::new);

        if(flight.getFlightStatus().equals(FlightStatus.CONFIRMED))
            throw new IllegalArgumentException();

        Ticket ticket = Optional.ofNullable(flight.getTicket(passenger))
                .orElseThrow(IllegalArgumentException::new);

        return flightCentral.getAlternativeFlights( ticket.getCategory(), flight.getDestiny()).stream().sorted(
                Comparator.comparing((Flight f) -> f.getBestAvailableCategory(ticket.getCategory()))
                        .thenComparingInt(Flight::getAvailableSeats).reversed()
                        .thenComparing(Flight::getFlightCode)
        ).collect(Collectors.toList());

    }

    @Override
    public void changeTicket(String passenger, String oldFlightCode, String newFlightCode) throws RemoteException {
        Flight oldFlight = Optional.ofNullable(flightCentral.getFlight(oldFlightCode))
                .orElseThrow(RemoteException::new);
        if(oldFlight.getFlightStatus().equals(FlightStatus.CONFIRMED)
                || !oldFlight.passengerExists(passenger))
            throw new IllegalArgumentException();
        Flight newFlight = Optional.ofNullable(flightCentral.getFlight(newFlightCode))
                .orElseThrow(RemoteException::new);
        Ticket oldTicket = oldFlight.getTicket(passenger);
        List<Flight> alternativeFlights = flightCentral
                .getAlternativeFlights( oldTicket.getCategory(), oldFlight.getDestiny());
        if(!alternativeFlights.contains(newFlight))
            throw new IllegalArgumentException();
        oldFlight.deletePassenger(passenger);
        newFlight.addTicket(oldTicket);
        flightCentral.notifyFlightChange(passenger,oldFlight,newFlight);
    }

    private int fromColumnCharacter(Character column){
        return column - 'A';
    }

}
