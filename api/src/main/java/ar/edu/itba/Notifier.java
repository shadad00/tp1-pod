package ar.edu.itba;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Seat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Notifier extends Remote {
    void notifyRegistration(Flight flight) throws RemoteException;
    void notifyConfirmation(Flight flight) throws RemoteException;
    void notifyCancellation(Flight flight) throws RemoteException;
    void notifyAssignation(Flight flight) throws RemoteException;
    void notifySeatChange(Seat originalSeat, Flight flight) throws  RemoteException;
    void notifyFlightChange(Flight oldFlight, Flight newFlight) throws RemoteException;
}
