package flightService.server;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlightCentral {

    // destinos -> vuelos
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


            flights.putIfAbsent(modelName,new ArrayList<>());
        flights.get(modelName).add(newFlight);
    public void addFlight(String )


}
