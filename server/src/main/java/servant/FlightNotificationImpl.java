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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class FlightNotificationImpl implements FlightNotification, FlightMonitor {

    private final FlightCentral flightCentral;
    private final ConcurrentHashMap<Flight, List< Pair<String, Notifier> >>registeredUsers;
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
            LOG.info("Flight not available for subscription for this user");
            throw new IllegalUserRegistration("Flight not available for subscription for this user");
        }

        registeredUsers.putIfAbsent(flight, Collections.synchronizedList(new ArrayList<>()));
        registeredUsers.get(flight).add(new Pair<>(passenger, notifier));


        executor.submit(() -> {
            try {
                notifier.notifyRegistration(flight.getFlightCode(),flight.getDestiny());
            } catch (RemoteException e) {
                LOG.info("Registration: Failed to callback with " + passenger + " registered at " + flight);
                throw new RuntimeException(e);
            }
        });


    }


    @Override
    public void notifyConfirmation(Flight flight){
        LOG.info("Confirmation callbacks being processed");

        if(registeredUsers.containsKey(flight) ) {
            for (Pair<String, Notifier> entries : registeredUsers.get(flight)) {
                executor.submit(() -> {
                    try {
                        entries.getValue().notifyConfirmation(flight.getFlightCode(), flight.getDestiny()
                                , flight.getTicket(entries.getKey()).getSeat());
                    } catch (RemoteException e) {
                        LOG.info("Confirmation: Failed to callback with " + entries.getKey() + " registered at " + flight);
                        throw new RuntimeException(e);
                    }
                });

            }
            registeredUsers.remove(flight);

        }
    }

    @Override
    public void notifyCancellation(Flight flight) {
        LOG.info("Cancellation flight callbacks being processed");

        if(registeredUsers.containsKey(flight)){
            for (Pair<String, Notifier> entries : registeredUsers.get(flight)) {
                executor.submit(() -> {
                    try {
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
        LOG.info("Assignation Callbacks being processed");

        if(registeredUsers.containsKey(flight))
            {
                LOG.info("contains user");
                for (Pair<String, Notifier> entries : registeredUsers.get(flight)) {
                    if (!entries.getKey().equals(passenger))
                        continue;
                    executor.submit(() -> {
                        try {
                            entries.getValue().notifyAssignation(flight.getFlightCode()
                                    , flight.getDestiny(), flight.getTicket(passenger).getSeat());
                        } catch (RemoteException e) {
                            LOG.info("Assignation: Failed to callback with " + passenger + " registered at " + flight);
                            throw new RuntimeException(e);
                        }
                    });
                }
            }

    }

    @Override
    public void notifySeatChange(String passenger, Seat originalSeat, Flight flight){
        LOG.info("Seat change callbacks being processed");

        if(registeredUsers.containsKey(flight)) {
            for (Pair<String, Notifier> entries : registeredUsers.get(flight)) {
                if (!entries.getKey().equals(passenger))
                    continue;
                executor.submit(() -> {
                    try {
                        entries.getValue().notifySeatChange(passenger, originalSeat, flight);
                    } catch (RemoteException e) {
                        LOG.info("Seat change: Failed to callback with " + passenger + " registered at " + flight);
                        throw new RuntimeException(e);
                    }
                });

            }
        }
    }

    @Override
    public void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight){
        LOG.info("Flight change callbacks being processed");

        if(registeredUsers.containsKey(oldFlight)){
            for (Pair<String, Notifier> entries : registeredUsers.get(oldFlight)) {
                if (!entries.getKey().equals(passenger))
                    continue;
                executor.submit(() -> {
                    try {
                        System.out.println("Running on " + Thread.currentThread());
                        entries.getValue().notifyFlightChange(oldFlight, newFlight);
                    } catch (RemoteException e) {
                        LOG.info("Flight change: failed to callback with " + passenger + " registered at " + oldFlight);
                        throw new RuntimeException(e);
                    }
                });
                //registeredUsers.get(oldFlight).remove(entries);
                registeredUsers.putIfAbsent(newFlight, Collections.synchronizedList(new ArrayList<>()));
                registeredUsers.get(newFlight).add(entries);

            }
        }
    }




}

