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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FlightNotificationImpl implements FlightNotification, FlightMonitor {

    private final FlightCentral flightCentral;
    private final Map<Flight, List< Pair<String, Notifier> > >registeredUsers;
    private static final Logger LOG = LoggerFactory.getLogger(FlightNotificationImpl.class);
    private static final int NUMBER_OF_THREADS = 8;
    private static final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);



    public FlightNotificationImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
        this.registeredUsers = new ConcurrentHashMap<>();
    }

    @Override
    public void registerUser(String flightCode, String passenger, Notifier notifier) throws IllegalUserRegistration, RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if(flight == null || notifier == null
                || passenger==null || flight.getStatus() == FlightStatus.CONFIRMED
                        || !flight.passengerExists(passenger)){
            throw new IllegalUserRegistration("Flight not available for subscription for this user");
        }
        synchronized (registeredUsers) {
            registeredUsers.putIfAbsent(flight, new ArrayList<>());
            registeredUsers.get(flight).add(new Pair<>(passenger, notifier));
        }

        executor.submit(() -> {
            try {
                System.out.println("Running on " + Thread.currentThread());
                notifier.notifyRegistration(flight.getFlightCode(),flight.getDestiny());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });


    }


    @Override
    public void notifyConfirmation(Flight flight){
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                 executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifyConfirmation(flight.getFlightCode(), flight.getDestiny()
                                , flight.getTicket(entries.getKey()).getSeat());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @Override
    public void notifyCancellation(Flight flight) {
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifyCancellation(flight.getFlightCode(), flight.getDestiny()
                                , flight.getTicket(entries.getKey()).getSeat());
                    } catch (RemoteException e) {
                        LOG.info("Cancellation: Failed to callback with " + entries.getKey() + " registered at " + flight);

                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @Override
    public void notifyAssignation(String passenger, Flight flight) {
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                if(!entries.getKey().equals(passenger))
                    continue;
                executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifyAssignation(flight.getFlightCode()
                                , flight.getDestiny(), flight.getTicket(passenger).getSeat());
                    } catch (RemoteException e) {
                        LOG.info("Assignation: failed to callback with " + passenger +" registered at "+flight);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @Override
    public void notifySeatChange(String passenger, Seat originalSeat, Flight flight){
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(flight, new ArrayList<>())) {
                if(!entries.getKey().equals(passenger))
                    continue;
                executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifySeatChange(passenger, originalSeat, flight);
                    } catch (RemoteException e) {
                        LOG.info("Seat change: failed to callback with " + passenger +" registered at "+ flight);
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    @Override
    public void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight) {
        synchronized (registeredUsers) {
            for (Pair<String, Notifier> entries : registeredUsers.getOrDefault(oldFlight, new ArrayList<>())) {
                if(!entries.getKey().equals(passenger))
                    continue;
                executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifyFlightChange(oldFlight, newFlight);
                    } catch (RemoteException e) {
                        LOG.info("Flight change: failed to callback with " + passenger +" registered at "+ oldFlight);
                        throw new RuntimeException(e);
                    }
                });

            }
        }
    }



}

