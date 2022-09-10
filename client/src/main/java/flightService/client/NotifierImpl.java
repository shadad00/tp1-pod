package flightService.client;

import ar.edu.itba.Notifier;
import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Seat;

import java.rmi.RemoteException;

public class NotifierImpl implements Notifier {
    @Override
    public void notifyRegistration(String flightCode, String destination) throws RemoteException {
        System.out.println("You are following flight " + flightCode + " with destination " + destination);
    }

    @Override
    public void notifyConfirmation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
        System.out.println("Your Flight "+ flightCode +"with destination " + destination +" was confirmed and your seat is " +
                currentSeat.getCategory() + " " + currentSeat.getRow() + currentSeat.getColumn() + " .");
    }

    @Override
    public void notifyCancellation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
        System.out.println("Your Flight "+
                flightCode  +
                "with destination " + destination +
                " was confirmed and your seat is " +
                currentSeat.getCategory() + " " +
                currentSeat.getRow() +
                currentSeat.getColumn() +
                " .");
    }

    @Override
    public void notifyAssignation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
        System.out.println("Your seat is" +
                currentSeat.getCategory() + " " +
                currentSeat.getRow() +
                currentSeat.getColumn() + "for Flight " + flightCode +"with destination "+ destination );
    }

    @Override
    public void notifySeatChange(Seat originalSeat, Flight flight) throws RemoteException {
       Seat newSeat =  flight.getPassengers().get(originalSeat.getPassenger());
        System.out.println("Your seat changed to " +
                newSeat.getCategory() +
                " " + newSeat.getRow() +
                " " + newSeat.getColumn()
                + " from " +  originalSeat.getCategory() +
                " " + originalSeat.getRow() +
                " " + originalSeat.getColumn() + "for Flight" +
                flight.getFlightCode() + " with destination " +  flight.getDestiny());
    }

    @Override
    public void notifyFlightChange(Flight oldFlight, Flight newFlight) throws RemoteException {
        System.out.println("Your ticket changed to Flight " +
                newFlight.getFlightCode() + "with destination " + newFlight.getDestiny()+
                " from Flight " + oldFlight.getFlightCode() +
                "with destination " + oldFlight.getDestiny());
    }
}
