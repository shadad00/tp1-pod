package ar.edu.itba.models;

import java.util.List;
import java.util.Map;

public class Airline {

    private final Map<String, List<Flight>> flights;

    public Airline(Map<String, List<Flight>> flights) {
        this.flights = flights;
    }

    public void addFlight(Flight flight){

    }


}
