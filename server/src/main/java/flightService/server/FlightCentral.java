package flightService.server;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import ar.edu.itba.remoteInterfaces.SeatMap;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FlightCentral implements FlightMonitor {

    // codigo de vuelo -> vuelos
    private final ConcurrentHashMap<String, Flight> flights;

    //modelos -> aviones
    private final ConcurrentHashMap<String, Plane> models;
    public static final String mutex_system = "mutex_cancellation";

    private FlightMonitor internalNotifier;
//    private final  FlightAdministration internalAdmin;
//    private final  SeatAssignation internalAssignation;
//    private final  SeatMap internalSeatMap;

    private final String mutex_notificator = "mutex_notificator";



    public FlightCentral() {
        flights = new ConcurrentHashMap<>();
        models = new ConcurrentHashMap<>();

    }

    public void setNotificator(FlightMonitor monitor){
        synchronized (mutex_notificator) {
            if (internalNotifier == null) {
                internalNotifier = monitor;
            }
        }
    }

    public Flight getFlight(String code){
        return flights.get(code);
    }

    public Map<String, Flight> getFlights() {
            return Collections.unmodifiableMap(flights);
    }

    public Plane getModels(String model) {
        return models.get(model);
    }

    public Flight addFlight(String flightCode, Flight newFlight){
        return flights.putIfAbsent(flightCode, newFlight);
    }


    public Plane addModel(String modelName, Plane plane){
        return models.putIfAbsent(modelName, plane);
    }

    public boolean flightExists(String flightCode){
        return flights.containsKey(flightCode);
    }

    public boolean modelExists(String model){
        return models.containsKey(model);
    }


    public List<Flight> getAlternativeFlights(SeatCategory category, String destiny, String selfFlightCode){
        return flights.values().stream()
                    .filter(
                            flight -> !flight.getFlightCode().equals(selfFlightCode)
                                    && flight.getStatus().equals(FlightStatus.PENDING)
                                    && flight.getDestiny().equals(destiny)
                                    ).collect(Collectors.toList());

    }

    public void notifyConfirmation(Flight flight){
        internalNotifier.notifyConfirmation(flight);
    }
    public void notifyCancellation(Flight flight){
        internalNotifier.notifyCancellation(flight);
    }
    public void notifyAssignation(String passenger, Flight flight) {
        internalNotifier.notifyAssignation(passenger,flight);
    }
    public void notifySeatChange(String passenger, Seat originalSeat, Flight flight) {
        internalNotifier.notifySeatChange(passenger, originalSeat, flight);
    }
    public void notifyFlightChange(String passenger, Flight oldFlight, Flight newFlight) {
        internalNotifier.notifyFlightChange(passenger, oldFlight, newFlight);
    }

    public List<AlternativeFlight> getAlternatives(String flightCode, String passenger){
        Flight flight = Optional.ofNullable(getFlight(flightCode))
                .orElseThrow(IllegalArgumentException::new);

        if(flight.getStatus().equals(FlightStatus.CONFIRMED))
            throw new IllegalArgumentException();

        Ticket ticket = Optional.ofNullable(flight.getTicket(passenger))
                .orElseThrow(IllegalArgumentException::new);

        List<Flight> flights =  getAlternativeFlights( ticket.getCategory(), flight.getDestiny(),flightCode);

        List<AlternativeFlight> alternativeFlights = new ArrayList<>();

        for (Flight f : flights) {
            int cant;
            for (SeatCategory category : SeatCategory.values()) {
                if(ticket.getCategory().compareTo(category) <= 0  && (cant=f.getAvailableSeatsByCategory(category) ) >0) {
                    alternativeFlights.add(new AlternativeFlight(cant, category, f.getFlightCode(), f.getDestiny()));
                }
            }
        }
        return alternativeFlights;
    }
}
