package flightService.server;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.FlightStatus;
import ar.edu.itba.models.Plane;
import ar.edu.itba.models.SeatCategory;

import java.util.*;

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

    public Plane getModels(String model) {
        return models.get(model);
    }



    public Flight addFlight(String flightCode, Flight newFlight){
        return flights.putIfAbsent(flightCode,newFlight);
    }


    public Plane addModel(String modelName, Plane plane){
        return models.putIfAbsent(modelName,plane);
    }

    public List<Flight> getSuspendedFlights(){
        List<Flight> suspended = new ArrayList<>();
        for ( Flight flight : this.flights.values()){
            if(flight.getFlightStatus().equals(FlightStatus.CANCELLED))
                suspended.add(flight);
        }
        return suspended;
    }

    public SortedSet<Flight> getAlternativeFlights(String destiny, SeatCategory category){
        SortedSet<Flight> alternativeFlights = new TreeSet<>();
        for ( Flight flight : this.flights.values()){
            if(flight.getDestiny().equals(destiny))
                alternativeFlights.add(flight);
        }
        return alternativeFlights;
    }


}
