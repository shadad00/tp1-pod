package flightService.server;

import ar.edu.itba.models.Flight;

import java.util.HashMap;
import java.util.Map;

public class FlightCentral {

    private final Map<String, Flight> flights;

    public FlightCentral() {
        flights = new HashMap<>();
    }

    public Flight getFlight(String code){
        return flights.get(code);
    }

}
