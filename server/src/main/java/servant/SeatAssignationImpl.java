package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatAssignationImpl implements SeatAssignation {

    private final FlightCentral flightCentral;

    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }



    @Override
    public String isSeatFree(String flightCode, Integer row, Character col) throws RemoteException {
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
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(IllegalArgumentException::new);
        Ticket passengerTicket = Optional.ofNullable(flight.getTicket(passenger)).orElseThrow(IllegalArgumentException::new);
        Seat oldSeat = passengerTicket.getSeat();

        if(flight.isSeatAvailable(row, fromColumnCharacter(col)) != null)
            throw new IllegalArgumentException("New seat is already taken.");

        flight.freePassengerSeat(passenger);
        assignSeatPrivate(flightCode, passenger, row, fromColumnCharacter(col) );
        flightCentral.notifySeatChange(passenger,oldSeat,flight);
    }

    @Override
    public String checkAlternativeFlights(String flightCode, String passenger) throws RemoteException {

        List<AlternativeFlight> alternativeFlights = flightCentral.getAlternatives(flightCode, passenger);

        StringBuilder stringBuilder = new StringBuilder();
        alternativeFlights.stream().sorted().forEach(stringBuilder::append);
        return stringBuilder.toString();

      }




    @Override
    public void changeTicket(String passenger, String oldFlightCode, String newFlightCode) throws RemoteException {
        System.out.println("old " + oldFlightCode);
        System.out.println("new:"  +newFlightCode);
        System.out.println("passenger " + passenger);
        Flight oldFlight = Optional.ofNullable(flightCentral.getFlight(oldFlightCode))
                .orElseThrow(IllegalArgumentException::new);
        if(oldFlight.getFlightStatus().equals(FlightStatus.CONFIRMED)
                || !oldFlight.passengerExists(passenger))
            throw new IllegalArgumentException();
        Flight newFlight = Optional.ofNullable(flightCentral.getFlight(newFlightCode))
                .orElseThrow(IllegalArgumentException::new);
        Ticket oldTicket = oldFlight.getTicket(passenger);
        List<Flight> alternativeFlights = flightCentral
                .getAlternativeFlights( oldTicket.getCategory(), oldFlight.getDestiny(),oldFlightCode);
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
