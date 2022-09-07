package ar.edu.itba.models;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Flight {
    private final String flightCode;
    private final String destiny;
    private final Plane plane;
    private final EnumMap<SeatCategory, Seat[][]> seats;

    private final Map<Passenger, Seat> passengers = new HashMap<>();

    private FlightStatus flightStatus;



    public Flight(String flightCode, String destiny, Plane plane, EnumMap<SeatCategory, Seat[][]> seats) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.plane = plane;
        this.seats = seats;
    }

    public Map<Passenger, Seat> getPassengers() {
        return passengers;
    }
    public FlightStatus getFlightStatus() {
        return flightStatus;
    }
}
