package servant;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Plane;
import ar.edu.itba.models.Seat;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.server.FlightCentral;

import javax.sql.rowset.Predicate;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.stream.Stream;

public class SeatAssignationImpl implements SeatAssignation {

    private final FlightCentral flightCentral;

    public SeatAssignationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }

    @Override
    public boolean isSeatFree(int row, char column, String flightCode) throws RemoteException {

        // TODO: use custom exceptions !

        Flight flight = Optional.ofNullable(flightCentral.getFlight(flightCode)).orElseThrow(RemoteException::new);

        return (Optional.ofNullable(flight.getSeat(row, column)).orElseThrow(RemoteException::new)).isFree();

    }

    @Override
    public void assignSeat(String flightCode, int row, char col, String passenger) throws RemoteException {
    //seria lo mismo que move pero sin la parte del remove?
        if(isSeatFree(row, col, flightCode))
            throw new RemoteException();

        Flight flight = flightCentral.getFlight(flightCode);
        if (flight == null) {
            throw new RemoteException();
        }

        if(!flight.getPassenger(passenger).getCategory().equals(flight.getPlane().getCategoryFromSeat(row, col)))
            throw new RemoteException();

//        if(flight.getStatus())

//        flight.addPassenger(passenger, row, col);

    }

    @Override
    public void movePassenger(String code, String passenger, int row, char column) throws RemoteException {
        Flight flight = flightCentral.getFlight(code);
//        flight.removePassenger(passenger);
        flight.addPassenger(passenger, row, column);
    }

    @Override
    public void checkAlternativeFlights(Flight flight, Passenger passenger) throws RemoteException {

    }

    @Override
    public void changeTicket(Passenger passenger, Flight original, Flight alternative) throws RemoteException {

    }
}
