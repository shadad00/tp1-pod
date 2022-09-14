package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.rmi.RemoteException;
import java.util.List;



public class SeatAssignationImpl implements SeatAssignation {

    private final FlightCentral flightCentral;
    private static final Logger LOG = LoggerFactory.getLogger(SeatAssignationImpl.class);


    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }

    @Override
    public String isSeatFree(String flightCode, Integer row, Character col) throws RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if (flight == null) {
            LOG.info(String.format("Flight %s does not exist",flightCode));
            throw new IllegalArgumentException("Flight does not exist");
        }
        return flight.isSeatAvailable(row, fromColumnCharacter(col));
    }


    @Override
    public void assignSeat(String flightCode, String passenger, Integer row, Character col) throws RemoteException, IllegalArgumentException, IllegalStateException {
        Flight flight = flightCentral.getFlight(flightCode);
        if(flight == null){
            LOG.info(String.format("Flight %s does not exist",flightCode));
            throw new IllegalArgumentException("Flight does not exist");
        }

        Ticket ticket = flight.getTicket(passenger);
        if(ticket == null){
            LOG.info(String.format("Invalid passenger %s in flight %s"),passenger,flightCode);
            throw new IllegalArgumentException("Invalid passenger in this flight");
        }

        if(ticket.hasSeat()) {
            LOG.info(String.format("Passenger %s has a seat already assigned",passenger));
            throw new IllegalArgumentException("This passenger has a seat already assigned");
        }

        synchronized (flight.getFlightCode()) {
            try {
                flight.assignSeat(ticket, row, fromColumnCharacter(col));
            } catch (IllegalArgumentException e) {
                LOG.info(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }
        flightCentral.notifyAssignation(passenger,flightCentral.getFlight(flightCode));

    }

    @Override
    public void movePassenger(String flightCode, String passenger, Integer row, Character col) throws RemoteException, IllegalArgumentException {

        Flight flight = flightCentral.getFlight(flightCode);
        if( flight == null){
            LOG.info(String.format("Flight %s does not exist",flightCode));
            throw new IllegalArgumentException("Invalid Flight Code");
        }

        Ticket passengerTicket = flight.getTicket(passenger);
        if(passengerTicket == null){
            LOG.info(String.format("Invalid passenger %s in flight %s"),passenger,flightCode);
            throw new IllegalArgumentException("Invalid Passenger for this flight");
        }

        Seat oldSeat = passengerTicket.getSeat();
        if( oldSeat == null){
            LOG.info(String.format("Passenger %s does not have a seat assigned",passenger));
            throw new IllegalArgumentException("Passenger does not have a seat assigned");
        }

        synchronized (flightCentral.getFlight(flightCode).getFlightCode()) {
            if(flight.isSeatAvailable(row, fromColumnCharacter(col)) != null) {
                LOG.info("New seat is already taken");
                throw new IllegalArgumentException("New seat is already taken");
            }
            try {
                flight.freeSeatByPassenger(passengerTicket);
                flight.assignSeat(passengerTicket, row, fromColumnCharacter(col));
            }catch (IllegalArgumentException e){
                LOG.info(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        flightCentral.notifySeatChange(passenger,oldSeat,flight);
    }

    @Override
    public String checkAlternativeFlights(String flightCode, String passenger) throws RemoteException {
        List<AlternativeFlight> alternativeFlights;
        try {
            alternativeFlights = flightCentral.getAlternatives(flightCode, passenger);
        }catch (IllegalArgumentException e){
            LOG.info(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }

        StringBuilder stringBuilder = new StringBuilder();
        alternativeFlights.stream().sorted().forEach(stringBuilder::append);
        return stringBuilder.toString();

      }

      @Override
    public void changeTicket(String passenger, String oldFlightCode, String newFlightCode) throws RemoteException {
        if (oldFlightCode.equals(newFlightCode)) {
            LOG.info("Change Ticket: flights are the same");
            throw new IllegalArgumentException("The flights are the same");
        }
        String min = oldFlightCode.compareTo(newFlightCode) < 0 ? oldFlightCode : newFlightCode;
        String max = oldFlightCode.compareTo(newFlightCode) < 0 ? newFlightCode : oldFlightCode;

        Flight oldFlight = flightCentral.getFlight(oldFlightCode);
        if (oldFlight == null) {
            LOG.info(String.format("Flight %s does not exist",oldFlightCode));
            throw new IllegalArgumentException("Invalid old flight code");
        }

        if(oldFlight.getStatus().equals(FlightStatus.CONFIRMED)){
            LOG.info(String.format("Flight %s is already confirmed",oldFlightCode));
            throw new IllegalArgumentException("Flight is already confirmed");
        }

        Flight newFlight = flightCentral.getFlight(newFlightCode);

        if (newFlight == null) {
            LOG.info(String.format("Flight %s does not exist",newFlightCode));
            throw new IllegalArgumentException("Invalid new flight code");
        }
        Ticket oldTicket = oldFlight.getTicket(passenger);
        if(oldTicket == null){
            LOG.info(String.format("Passenger %s does not exist in flight %s",passenger,oldFlight));
            throw new IllegalArgumentException("Passenger does not exist in this flight");
        }

        synchronized (flightCentral.getFlight(min).getFlightCode()){
            synchronized (flightCentral.getFlight(max).getFlightCode()){
                List<Flight> alternativeFlights = flightCentral
                        .getAlternativeFlights( oldTicket.getCategory(), oldFlight.getDestiny(),oldFlightCode);
                if(!alternativeFlights.contains(newFlight)) {
                    LOG.info("Invalid new flight code");
                    throw new IllegalArgumentException("Invalid new flight code");
                }
                try {
                    oldFlight.deletePassengerTicket(passenger);
                    newFlight.addTicket(oldTicket);
                }catch (IllegalArgumentException e){
                    LOG.info(e.getMessage());
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }

        flightCentral.notifyFlightChange(passenger,oldFlight,newFlight);

    }

    private int fromColumnCharacter(Character column){
        return column - 'A';
    }



}
