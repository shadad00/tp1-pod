package flightService.server;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;

import java.rmi.RemoteException;

public interface FlightMonitor {
    void notifyConfirmation(Flight flight);
    void notifyCancellation(Flight flight);
    void notifyAssignation(String passenger, Flight flight) ;
    void notifySeatChange(String passenger, Seat originalSeat, Flight flight) ;
    void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight) ;
}
