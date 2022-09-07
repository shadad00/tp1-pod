package flightService.server;

import ar.edu.itba.Notifier;
import ar.edu.itba.exceptions.IllegalUserRegistration;
import ar.edu.itba.models.Flight;
import ar.edu.itba.models.FlightStatus;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;
import ar.edu.itba.remoteInterfaces.FlightNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FlightNotificationImpl implements FlightNotification, FlightMonitor {

    private final FlightCentral flightCentral;
    private final Map<Flight, Map<Passenger, Notifier> > registeredUsers;
    private static final Logger LOG = LoggerFactory.getLogger(FlightNotificationImpl.class);


    public FlightNotificationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerUser(String flightCode, Passenger passenger, Notifier notifier) throws IllegalUserRegistration, RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if(flight == null || notifier == null || flight.getFlightStatus() == FlightStatus.CONFIRMED || flight.getPassengers().get(passenger) == null){
            throw new IllegalUserRegistration("Flight not available for subscription for this user");
        }
        registeredUsers.putIfAbsent(flight,new HashMap<>());
        registeredUsers.get(flight).put(passenger,notifier);
        notifier.notifyRegistration(flight);
    }


    @Override
    public void notifyConfirmation(Flight flight){
        for(Map.Entry<Passenger,Notifier> entries: this.registeredUsers.getOrDefault(flight,new HashMap<>()).entrySet())
            try{
                entries.getValue().notifyConfirmation(flight);
            }catch (RemoteException remoteException){
                LOG.info("Confirmation: Failed to callback with " + entries.getKey() +" registered at "+flight);
            }
    }

    @Override
    public void notifyCancellation(Flight flight) {
        for(Map.Entry<Passenger,Notifier> entries: this.registeredUsers.getOrDefault(flight,new HashMap<>()).entrySet())
            try{
                entries.getValue().notifyCancellation(flight);
            }catch (RemoteException remoteException){
                LOG.info("Cancellation: Failed to callback with " + entries.getKey() +" registered at "+flight);
            }
    }

    @Override
    public void notifyAssignation(Passenger passenger, Flight flight) {
        Map<Passenger,Notifier> flightSubscriber = this.registeredUsers.get(flight);
        if( flightSubscriber!= null && flightSubscriber.get(passenger) != null) {
            try {
                flightSubscriber.get(passenger).notifyAssignation(flight);
            } catch (RemoteException e) {
                LOG.info("Assignation: failed to callback with " + passenger +" registered at "+flight);
            }
        }
    }

    @Override
    public void notifySeatChange(Passenger passenger, Seat originalSeat, Flight flight){
        Map<Passenger,Notifier> flightSubscriber = this.registeredUsers.get(flight);
        if( flightSubscriber!= null && flightSubscriber.get(passenger) != null) {
            try {
                flightSubscriber.get(passenger).notifySeatChange(originalSeat, flight);
            } catch (RemoteException e) {
                LOG.info("Seat change: failed to callback with " + passenger +" registered at "+ flight);
            }
        }
    }

    @Override
    public void notifyFlightChange(Passenger passenger, Flight oldFlight, Flight newFlight) {

        Map<Passenger,Notifier> flightSubscriber = this.registeredUsers.get(oldFlight);
        if( flightSubscriber!= null && flightSubscriber.get(passenger) != null) {
            try {
                flightSubscriber.get(passenger).notifyFlightChange(oldFlight, newFlight);
            } catch (RemoteException e) {
                LOG.info("Flight change: failed to callback with " + passenger +" registered at "+ oldFlight);
            }
        }
    }



}

