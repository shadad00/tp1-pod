package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;


public class SeatAssignationImpl implements SeatAssignation {

    private final FlightCentral flightCentral;
    private static final Logger LOG = LoggerFactory.getLogger(SeatAssignationImpl.class);


    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }

    @Override
    public String isSeatFree(String flightCode, Integer row, Character col) throws RemoteException {
        return flightCentral.getFlight(flightCode).isSeatAvailable(row, fromColumnCharacter(col));
    }

    @Override
    public void assignSeat(String flightCode, String passenger, Integer row, Character col) throws RemoteException {

        synchronized (flightCentral.getFlight(flightCode).getFlightCode()) {
                if (!assignSeatPrivate(flightCode,passenger,row, fromColumnCharacter(col) )) {
                    LOG.info("Couldn't assign passenger" + passenger + " to seat " + row + col);
                }
        }
        flightCentral.notifyAssignation(passenger,flightCentral.getFlight(flightCode));
    }

    private boolean assignSeatPrivate(String flightCode, String passenger, Integer row, Integer col){
        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode))
                .orElseThrow(IllegalArgumentException::new);
        if(!flight.getStatus().equals(FlightStatus.PENDING))
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

        synchronized (flightCentral.getFlight(flightCode).getFlightCode()) {
            flight.freeSeatByPassenger(passenger);
            assignSeatPrivate(flightCode, passenger, row, fromColumnCharacter(col));
        }

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
        LOG.info("old " + oldFlightCode);
        LOG.info("new:"  +newFlightCode);
        LOG.info("passenger " + passenger);

        if (oldFlightCode.equals(newFlightCode)) {
            throw new IllegalArgumentException("The flights are the same");
        }
        String min = oldFlightCode.compareTo(newFlightCode) < 0 ? oldFlightCode : newFlightCode;
        String max = oldFlightCode.compareTo(newFlightCode) < 0 ? newFlightCode : oldFlightCode;

        Flight oldFlight = Optional.ofNullable(flightCentral.getFlight(oldFlightCode))
                .orElseThrow(IllegalArgumentException::new);
        if(oldFlight.getStatus().equals(FlightStatus.CONFIRMED)
                || !oldFlight.passengerExists(passenger))
            throw new IllegalArgumentException();
        Flight newFlight = Optional.ofNullable(flightCentral.getFlight(newFlightCode))
                .orElseThrow(IllegalArgumentException::new);
        Ticket oldTicket = oldFlight.getTicket(passenger);
        List<Flight> alternativeFlights = flightCentral
                .getAlternativeFlights( oldTicket.getCategory(), oldFlight.getDestiny(),oldFlightCode);
        if(!alternativeFlights.contains(newFlight))
            throw new IllegalArgumentException();

        synchronized (flightCentral.getFlight(min).getFlightCode()){
            synchronized (flightCentral.getFlight(max).getFlightCode()){
                oldFlight.deletePassengerTicket(passenger);
                newFlight.addTicket(oldTicket);
            }
        }

        flightCentral.notifyFlightChange(passenger,oldFlight,newFlight);

    }

    private int fromColumnCharacter(Character column){
        return column - 'A';
    }



}
