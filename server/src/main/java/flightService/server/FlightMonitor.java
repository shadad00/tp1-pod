package flightService.server;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;

import java.rmi.RemoteException;

public interface FlightMonitor {
    void notifyConfirmation(Flight flight);
    void notifyCancellation(Flight flight);
    void notifyAssignation(Passenger passenger, Flight flight) ;
    void notifySeatChange(Passenger passenger, Seat originalSeat, Flight flight) ;
    void notifyFlightChange(Passenger passenger, Flight oldFlight, Flight newFlight) ;
}
