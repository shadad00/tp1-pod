package flightService.client;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Seat;
import ar.edu.itba.remoteInterfaces.Notifier;

import java.rmi.RemoteException;

public class NotifierImpl implements Notifier {
    @Override
    public void notifyRegistration(String flightCode, String destination) throws RemoteException {
        System.out.println("You are following flight " + flightCode + " with destination " + destination);
    }

    @Override
    public void notifyConfirmation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
       String output = "Your Flight "+ flightCode +" with destination " + destination +" was confirmed";
       if(currentSeat!=null)
           output+=" and your seat is " +
        currentSeat.getCategory() + " " + currentSeat.getRow() + currentSeat.getCharColumn() + " .";
        System.out.println( output);
        System.exit(0);
    }

    @Override
    public void notifyCancellation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
        String output = "Your Flight "+
                flightCode  +
                " with destination " + destination +
                " was cancelled";
        if(currentSeat != null){
            output += " and your seat is " +
                    currentSeat.getCategory() + " " +
                    currentSeat.getRow() +
                    currentSeat.getCharColumn() +
                    " .";
        }

        System.out.println(output);
    }

    @Override
    public void notifyAssignation(String flightCode, String destination, Seat currentSeat) throws RemoteException {
        System.out.println("seat " + currentSeat);
        System.out.println("Your seat is " +
                currentSeat.getCategory() + " " +
                currentSeat.getRow() +
                currentSeat.getCharColumn() + " for Flight " + flightCode +" with destination "+ destination );
    }

    @Override
    public void notifySeatChange(String passenger, Seat originalSeat, Flight flight) throws RemoteException {
       Seat newSeat =  flight.getTicket(passenger).getSeat();
        System.out.println("Your seat changed to " +
                newSeat.getCategory() +
                " " + newSeat.getRow() +
                newSeat.getCharColumn()
                + " from " +  originalSeat.getCategory() +
                " " + originalSeat.getRow() +
                 originalSeat.getCharColumn() + " for Flight " +
                flight.getFlightCode() + " with destination " +  flight.getDestiny());
    }

    @Override
    public void notifyFlightChange(Flight oldFlight, Flight newFlight) throws RemoteException {
        System.out.println("Your ticket changed to Flight " +
                newFlight.getFlightCode() + " with destination " + newFlight.getDestiny()+
                " from Flight " + oldFlight.getFlightCode() +
                " with destination " + oldFlight.getDestiny());
        System.out.println("You are following flight " + newFlight.getFlightCode() + " with destination " + newFlight.getDestiny());

    }
}
