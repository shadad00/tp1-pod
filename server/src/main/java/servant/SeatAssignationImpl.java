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
            LOG.info("Flight does not exist");
            throw new IllegalArgumentException("Flight does not exist");
        }
        return flight.isSeatAvailable(row, fromColumnCharacter(col));
    }


    @Override
    public void assignSeat(String flightCode, String passenger, Integer row, Character col) throws RemoteException, IllegalArgumentException, IllegalStateException {
        Flight flight = flightCentral.getFlight(flightCode);
        if(flight == null){
            LOG.info("Flight does not exist");
            throw new IllegalArgumentException("Flight does not exist");
        }

        Ticket ticket = flight.getTicket(passenger);
        if(ticket == null){
            LOG.info("Invalid passenger in this flight");
            throw new IllegalArgumentException("Invalid passenger in this flight");
        }

        if(ticket.hasSeat()) {
            LOG.info("This passenger has a seat already assigned");
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

//                if (!assignSeatPrivate(flight,passenger,row, fromColumnCharacter(col) )) {
//                    LOG.info("Couldn't assign passenger" + passenger + " to seat " + row + col);
//                    throw new IllegalArgumentException("Couldn't assign passenger" + passenger + " to seat " + row + col);
//                }

    }

//    private boolean assignSeatPrivate(Flight flight, String passenger, Integer row, Integer col){
////        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode))
////                .orElseThrow(IllegalArgumentException::new);
//        if(!flight.getStatus().equals(FlightStatus.PENDING))
//            throw new IllegalArgumentException("Cannot assign seat to a flight that is not pending");
//        Ticket passengerTicket = Optional.ofNullable(flight.getTicket(passenger))
//                .orElseThrow(IllegalArgumentException::new);
//        if(passengerTicket.hasSeat())
//            throw new IllegalArgumentException("Invalid Seat");
//
//        return flight.assignSeat(passenger, row, col);
//    }


    @Override
    public void movePassenger(String flightCode, String passenger, Integer row, Character col) throws RemoteException, IllegalArgumentException {

        Flight flight = flightCentral.getFlight(flightCode);
        if( flight == null){
            LOG.info("Invalid Flight Code");
            throw new IllegalArgumentException("Invalid Flight Code");
        }

        Ticket passengerTicket = flight.getTicket(passenger);
        if(passengerTicket == null){
            LOG.info("Invalid Passenger for this flight");
            throw new IllegalArgumentException("Invalid Passenger for this flight");
        }

        Seat oldSeat = passengerTicket.getSeat();
        if( oldSeat == null){
            LOG.info("Passenger does not have a seat assigned");
            throw new IllegalArgumentException("Passenger does not have a seat assigned");
        }

        if(flight.isSeatAvailable(row, fromColumnCharacter(col)) != null) {
            LOG.info("New seat is already taken");
            throw new IllegalArgumentException("New seat is already taken");
        }

        synchronized (flightCentral.getFlight(flightCode).getFlightCode()) {
            try {
                flight.freeSeatByPassenger(passengerTicket);
                flight.assignSeat(passengerTicket, row, fromColumnCharacter(col));
            }catch (IllegalArgumentException e){
                LOG.info(e.getMessage());
                throw new IllegalArgumentException(e.getMessage());
            }
//            assignSeatPrivate(flight, passenger, row, fromColumnCharacter(col));
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
        LOG.info("old: " + oldFlightCode);
        LOG.info("new: "  +newFlightCode);
        LOG.info("passenger: " + passenger);

        if (oldFlightCode.equals(newFlightCode)) {
            LOG.info("The flights are the same");
            throw new IllegalArgumentException("The flights are the same");
        }
        String min = oldFlightCode.compareTo(newFlightCode) < 0 ? oldFlightCode : newFlightCode;
        String max = oldFlightCode.compareTo(newFlightCode) < 0 ? newFlightCode : oldFlightCode;

        Flight oldFlight = flightCentral.getFlight(oldFlightCode);
        if (oldFlight == null) {
            LOG.info("Invalid old flight code");
            throw new IllegalArgumentException("Invalid old flight code");
        }

        if(oldFlight.getStatus().equals(FlightStatus.CONFIRMED)){
            LOG.info("Flight is already confirmed");
            throw new IllegalArgumentException("Flight is already confirmed");
        }

//        if( !oldFlight.passengerExists(passenger)){
//            LOG.info("Passenger does not exist in this flight");
//            throw new IllegalArgumentException("Passenger does not exist in this flight");
//        }

        Flight newFlight = flightCentral.getFlight(newFlightCode);

        if (newFlight == null) {
            LOG.info("Invalid new flight code");
            throw new IllegalArgumentException("Invalid new flight code");
        }
        Ticket oldTicket = oldFlight.getTicket(passenger);
        if(oldTicket == null){
            LOG.info("Passenger does not exist in this flight");
            throw new IllegalArgumentException("Passenger does not exist in this flight");
        }
        List<Flight> alternativeFlights = flightCentral
                .getAlternativeFlights( oldTicket.getCategory(), oldFlight.getDestiny(),oldFlightCode);
        if(!alternativeFlights.contains(newFlight)) {
            LOG.info("Invalid new flight code");
            throw new IllegalArgumentException("Invalid new flight code");
        }

        synchronized (flightCentral.getFlight(min).getFlightCode()){
            synchronized (flightCentral.getFlight(max).getFlightCode()){
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
