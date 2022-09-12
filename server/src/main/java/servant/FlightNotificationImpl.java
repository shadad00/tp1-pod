package servant;

import ar.edu.itba.exceptions.IllegalUserRegistration;
import ar.edu.itba.models.Flight;
import ar.edu.itba.models.FlightStatus;
import ar.edu.itba.models.Seat;
import ar.edu.itba.remoteInterfaces.FlightNotification;
import flightService.server.FlightCentral;
import flightService.server.FlightMonitor;
import ar.edu.itba.remoteInterfaces.Notifier;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class FlightNotificationImpl implements FlightNotification, FlightMonitor {

    private final FlightCentral flightCentral;
    private final Map<Flight, List< Pair<String, Notifier> > >registeredUsers;
    private static final Logger LOG = LoggerFactory.getLogger(FlightNotificationImpl.class);


    public FlightNotificationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerUser(String flightCode, String passenger, Notifier notifier) throws IllegalUserRegistration, RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if(flight == null || notifier == null
                || passenger==null || flight.getFlightStatus() == FlightStatus.CONFIRMED
                        || !flight.passengerExists(passenger)){
            throw new IllegalUserRegistration("Flight not available for subscription for this user");
        }
        synchronized (registeredUsers) {
            registeredUsers.putIfAbsent(flight, new ArrayList<>());
            registeredUsers.get(flight).add(new Pair<>(passenger, notifier));
        }
        notifier.notifyRegistration(flight.getFlightCode(),flight.getDestiny());
    }


    @Override
    public void notifyConfirmation(Flight flight){
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                try {
                    entries.getValue().notifyConfirmation(flight.getFlightCode(), flight.getDestiny()
                            , flight.getTicket(entries.getKey()).getSeat());
                } catch (RemoteException remoteException) {
                    LOG.info("Confirmation: Failed to callback with " + entries.getKey() + " registered at " + flight);
                }
            }
        }
    }

    @Override
    public void notifyCancellation(Flight flight) {
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                try {
                    entries.getValue().notifyCancellation(flight.getFlightCode(), flight.getDestiny()
                            , flight.getTicket(entries.getKey()).getSeat());
                } catch (RemoteException remoteException) {
                    LOG.info("Cancellation: Failed to callback with " + entries.getKey() + " registered at " + flight);
                }
            }
        }
    }

    @Override
    public void notifyAssignation(String passenger, Flight flight) {
        /*List < Pair<String,Notifier> > flightSubscriber = registeredUsers.get(flight);
        if( flightSubscriber!= null && !flightSubscriber.isEmpty()) {
            try {
                for(Pair<String,Notifier> entries : flightSubscriber)
                    entries.getValue().notifyAssignation(flight.getFlightCode()
                        , flight.getDestiny(), flight.getTicket(passenger).getSeat());
            } catch (RemoteException e) {
                LOG.info("Assignation: failed to callback with " + passenger +" registered at "+flight);
            }
        }*/
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                try {
                    entries.getValue().notifyAssignation(flight.getFlightCode()
                            , flight.getDestiny(), flight.getTicket(passenger).getSeat());
                } catch (RemoteException remoteException) {
                    LOG.info("Assignation: failed to callback with " + passenger +" registered at "+flight);
                }
            }
        }
    }

    @Override
    public void notifySeatChange(String passenger, Seat originalSeat, Flight flight){

        /*List < Pair<String,Notifier> > flightSubscriber = this.registeredUsers.get(flight);
        if( flightSubscriber!= null && !flightSubscriber.isEmpty()) {
            try {
                for(Pair<String,Notifier> entries : flightSubscriber)
                    entries.getValue().notifySeatChange(passenger, originalSeat, flight);
            } catch (RemoteException e) {
                LOG.info("Seat change: failed to callback with " + passenger +" registered at "+ flight);
            }
        }*/

        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                try {
                    entries.getValue().notifySeatChange(passenger, originalSeat, flight);
                } catch (RemoteException remoteException) {
                    LOG.info("Seat change: failed to callback with " + passenger +" registered at "+ flight);
                }
            }
        }
    }

    @Override
    public void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight) {

        /*List < Pair<String,Notifier> > flightSubscriber = this.registeredUsers.get(oldFlight);

        if( flightSubscriber!= null && !flightSubscriber.isEmpty()) {
            try {
                for(Pair<String,Notifier> entries : flightSubscriber)
                    entries.getValue().notifyFlightChange(oldFlight, newFlight);
            } catch (RemoteException e) {
                LOG.info("Flight change: failed to callback with " + passenger +" registered at "+ oldFlight);
            }
        }*/

        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(oldFlight, new ArrayList<>())) {
                try {
                    entries.getValue().notifyFlightChange(oldFlight, newFlight);
                } catch (RemoteException remoteException) {
                    LOG.info("Flight change: failed to callback with " + passenger +" registered at "+ oldFlight);
                }
            }
        }
    }



}

