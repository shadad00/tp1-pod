package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Seat;
import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.models.Ticket;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Exposed interface by server to provide notification service by callbacks.
 * */

public interface Notifier extends Remote {
    void notifyRegistration(String flightCode, String destination) throws RemoteException;
    void notifyConfirmation(String flightCode, String destination, Seat currentSeat) throws RemoteException;
    void notifyCancellation(String flightCode, String destination, Seat currentSeat) throws RemoteException;
    void notifyAssignation(String flightCode, String destination, Seat currentSeat) throws RemoteException;
    void notifySeatChange(String passenger, Seat originalSeat, Flight flight) throws  RemoteException;
    void notifyFlightChange(Flight oldFlight, Flight newFlight) throws RemoteException;
}
