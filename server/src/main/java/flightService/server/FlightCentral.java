package flightService.server;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FlightCentral implements FlightMonitor {

    // codigo de vuelo -> vuelos
    private final ConcurrentHashMap<String, Flight> flights;

    //modelos -> aviones
    private final ConcurrentHashMap<String, Plane> models;

    private FlightMonitor internalNotifier;

    private final static String mutex_notificator = "mutex_notificator";



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

    public void addFlight(String flightCode, Flight newFlight){
        flights.putIfAbsent(flightCode, newFlight);
    }


    public void addModel(String modelName, Plane plane){
        models.putIfAbsent(modelName, plane);
    }

    public boolean flightExists(String flightCode){
        return flights.containsKey(flightCode);
    }

    public boolean modelExists(String model){
        return models.containsKey(model);
    }


    public List<Flight> getAlternativeFlights( String destiny, String selfFlightCode){
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
                .orElseThrow(() -> new IllegalArgumentException("Flight does not exist"));

        if(flight.getStatus().equals(FlightStatus.CONFIRMED))
            throw new IllegalArgumentException("Flight is already confirmed");

        Ticket ticket = Optional.ofNullable(flight.getTicket(passenger))
                .orElseThrow(() -> new IllegalArgumentException("Passenger does not exist in this flight"));

        List<Flight> flights =  getAlternativeFlights(flight.getDestiny(),flightCode);

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
