package flightService.server;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.AlternativeFlight;

import java.util.*;
import java.util.stream.Collectors;

public class FlightCentral implements FlightMonitor{

    // codigo de vuelo -> vuelos
    private final Map<String, Flight> flights;

    //modelos -> aviones
    private final Map<String, Plane> models;

    private  FlightMonitor internalNotifier;

    public FlightCentral() {
        flights = new HashMap<>();
        models = new HashMap<>();
    }

    public void setNotificator(FlightMonitor monitor){
        internalNotifier= monitor;
    }


    public FlightMonitor getFlightMonitor(){
        return this.internalNotifier;
    }

    public Flight getFlight(String code){
        synchronized (flights) {
            return flights.get(code);
        }
    }

    public Map<String, Flight> getFlights() {
        synchronized (flights) {
            return flights;
        }
    }

    public Plane getModels(String model) {
        synchronized (models) {
            return models.get(model);
        }
    }

    public Flight addFlight(String flightCode, Flight newFlight){
        synchronized (flights) {
            return flights.putIfAbsent(flightCode, newFlight);
        }
    }


    public Plane addModel(String modelName, Plane plane){
        synchronized (models) {
            return models.putIfAbsent(modelName, plane);
        }
    }

    public boolean flightExists(String flightCode){
        synchronized (flights) {
            return flights.containsKey(flightCode);
        }
    }

    public boolean modelExists(String model){
        synchronized (models) {
            return models.containsKey(model);
        }
    }


    public List<Flight> getAlternativeFlights(SeatCategory category, String destiny, String selfFlightCode){
        synchronized (flights) {
            return flights.values().stream()
                    .filter(
                            flight -> !flight.getFlightCode().equals(selfFlightCode)
                                    && flight.getStatus().equals(FlightStatus.PENDING)
                                    && flight.getDestiny().equals(destiny)
                                    ).collect(Collectors.toList());
        }
    }

    //TODO: chequear si están bien sincronizadas las notifications
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
