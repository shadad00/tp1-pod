package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import ar.edu.itba.models.utils.RowColumnPair;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;

public class FlightAdministrationImpl implements FlightAdministration {

    private final FlightCentral flightCentral;
    private static Logger LOG = LoggerFactory.getLogger(FlightAdministrationImpl.class);
    private final String mutex_models="MODELS";
    private final String mutex_flights="FLIGHTS";

    public FlightAdministrationImpl(FlightCentral flightCentral){
        this.flightCentral=flightCentral;
    }

    @Override
    public void addPlaneModel(String model, EnumMap<SeatCategory, RowColumnPair> categories) throws RemoteException {
        EnumMap<SeatCategory, PlaneCategoryInformation> categoryDescriptors = new EnumMap<>(SeatCategory.class);

        int totalRows = 0;
        for (SeatCategory category : SeatCategory.values()
             ) {
            RowColumnPair pair = categories.get(category);
            if(pair != null && pair.getRow() > 0 && pair.getColumn() > 0) {
                categoryDescriptors.put(category, new PlaneCategoryInformation(category, totalRows+1, totalRows + pair.getRow(), pair.getColumn()));
                totalRows += pair.getRow();
            }
        }

        if(totalRows <= 0) {
            LOG.info("Invalid rows and columns");
            throw new IllegalArgumentException("Cannot add model "+ model);
        }
        synchronized (mutex_models) {
            if (flightCentral.getModels(model) != null) {
                LOG.info("Model already exists");
                throw new IllegalArgumentException("Cannot add model "+ model);
            }
            flightCentral.addModel(model, new Plane(model, categoryDescriptors));
        }
    }

    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, ConcurrentHashMap<String, Ticket> tickets) throws RemoteException {
        synchronized (mutex_flights) {
            if (!flightCentral.modelExists(modelName)
                    || flightCentral.flightExists(flightCode)) {
                LOG.info("model or flight already exists " + modelName + " " + flightCode);
                throw new IllegalArgumentException("Cannot add flight " + flightCode);
            }

            flightCentral.addFlight(flightCode, new Flight(flightCode, destinationAirportCode, flightCentral.getModels(modelName), tickets));
        }
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return Optional.ofNullable(flightCentral.getFlight(flightCode))
                .orElseThrow(() -> new IllegalArgumentException("Invalid Flight Code")).getStatus();
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if (flight == null) {
            LOG.info("Invalid flight code");
            throw new IllegalArgumentException("Invalid Flight Code");
        }
        synchronized (flight.getFlightCode()) {
            if (!flight.getStatus().equals(FlightStatus.PENDING)){
                LOG.info("Flight is not pending");
                throw new IllegalArgumentException("Flight is not pending");
            }
            flight.setStatus(FlightStatus.CONFIRMED);
        }
        flightCentral.notifyConfirmation(flight);
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        Flight flight = flightCentral.getFlight(flightCode);
        if (flight == null) {
            LOG.info("Invalid flight code");
            throw new IllegalArgumentException("Invalid Flight Code");
        }

        synchronized (flight.getFlightCode()) {
            if (!flight.getStatus().equals(FlightStatus.PENDING)){
                LOG.info("Flight is not pending");
                throw new IllegalArgumentException("Flight is not pending");
            }
            flight.setStatus(FlightStatus.CANCELLED);
        }
        flightCentral.notifyCancellation(flight);
    }


    //
    @Override
    public synchronized String forceTicketChangeForCancelledFlights() throws RemoteException {

        AtomicInteger cantMoved = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
            flightCentral.getFlights().values().stream().filter(flight -> flight.getStatus().equals(FlightStatus.CANCELLED))
                    .sorted(Comparator.comparing(Flight::getFlightCode))
                    //for each cancelled flight ordered by flightCode.
                    .forEach(
                            oldFlight -> oldFlight.getTickets().stream().sorted(Comparator.comparing(Ticket::getPassenger))
                                    //For each passenger in the flight order by Passenger's name.
                                    .forEach(
                                            ticket -> {

                                                List<AlternativeFlight> alternatives = flightCentral.getAlternatives(oldFlight.getFlightCode(), ticket.getPassenger());

                                                if (alternatives.isEmpty()) {
                                                    LOG.info("No alternatives for " + ticket.getPassenger() + " to " + oldFlight.getDestiny());
                                                    sb.append("Cannot find alternative flight for ").append(ticket.getPassenger()).append(" to ").append(oldFlight.getDestiny()).append("\n");
                                                } else {
                                                    cantMoved.getAndIncrement();
                                                    List<Flight> collect = alternatives.stream().sorted().map((alt) -> flightCentral.getFlight(alt.getFlightCode())).collect(Collectors.toList());

                                                    Flight aux=null;

                                                    for (Flight flight : collect) {
                                                        synchronized (flight.getFlightCode()) {
                                                            if (!flight.getStatus().equals(FlightStatus.CANCELLED)) {
                                                                    oldFlight.deletePassengerTicket(ticket.getPassenger());
                                                                    ticket.clearSeat();
                                                                    flight.addTicket(ticket);
                                                                    LOG.info("Moving " + ticket.getPassenger() + " to " + flight.getFlightCode());
                                                                    aux=flight;
                                                                    break;
                                                            }
                                                        }
                                                    }

                                                    if(aux == null)
                                                        sb.append("Cannot find alternative flight for").append(ticket.getPassenger()).append(" to ").append(oldFlight.getDestiny()).append("\n");
                                                    else flightCentral.notifyFlightChange(ticket.getPassenger(),oldFlight,aux);

                                                }
                                            }
                                    )
                    );

        StringBuilder out = new StringBuilder(cantMoved + "tickets where changed.\n").append(sb);

        return out.toString();

    }

}
