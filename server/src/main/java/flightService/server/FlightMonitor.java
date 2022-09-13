package flightService.server;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Seat;

import java.rmi.RemoteException;

/**
 * Used internally on server side to notification service.
 **/
public interface FlightMonitor {
    /**
     * Notify a flight has been confirmed.
     **/
    void notifyConfirmation(Flight flight);

    /**
     * Notify a flight has been cancelled
     * */
    void notifyCancellation(Flight flight);

    /**
     * Notify a passenger has chosen a seat on flight.
     * */
    void notifyAssignation(String passenger, Flight flight) ;

    /**
     * Notify a passenger has changed its seat on flight.
     * */
    void notifySeatChange(String passenger, Seat originalSeat, Flight flight) ;

    /**
     * Notify a passenger has changed oldFlight to newflight
     * */
    void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight) ;
}
