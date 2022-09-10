package flightService.server;

import ar.edu.itba.models.*;
import jdk.jfr.DataAmount;

import java.util.*;
import java.util.stream.Collectors;

public class FlightCentral {

    // codigo de vuelo -> vuelos
    private final Map<String, Flight> flights;

    //modelos -> aviones
    private final Map<String, Plane> models;

    public FlightCentral() {
        flights = new HashMap<>();
        models = new HashMap<>();
    }

    public Flight getFlight(String code){
        return flights.get(code);
    }

    public Map<String, Flight> getFlights() {
        return flights;
    }

    public Plane getModels(String model) {
        return models.get(model);
    }

    public Flight addFlight(String flightCode, Flight newFlight){
        return flights.putIfAbsent(flightCode,newFlight);
    }


    public Plane addModel(String modelName, Plane plane){
        return models.putIfAbsent(modelName,plane);
    }

    public boolean flightExists(String flightCode){
        return flights.containsKey(flightCode);
    }

    public boolean modelExists(String model){
        return models.containsKey(model);
    }


    public List<Flight> getAlternativeFlights(SeatCategory category, String destiny){
            return this.flights.values().stream()
                .filter(
                        flight -> flight.getStatus().equals(FlightStatus.PENDING)
                                && flight.getDestiny().equals(destiny)
                                && flight.hasAvailableSeatsForCategoryOrLower(category)).collect(Collectors.toList());
    }


}
