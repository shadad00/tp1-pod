package ar.edu.itba.models;

import java.util.EnumMap;

public class Flight {
    private final String flightCode;
    private final String destiny;
    private final Plane plane;
    private final EnumMap<SeatCategory, Seat[][]> seats;
    private FlightStatus flightStatus = FlightStatus.PENDING;


    public Flight(String flightCode, String destiny, Plane plane, EnumMap<SeatCategory, Seat[][]> seats) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.plane = plane;
        this.seats = seats;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getDestiny() {
        return destiny;
    }

    public Plane getPlane() {
        return plane;
    }

    public EnumMap<SeatCategory, Seat[][]> getSeats() {
        return seats;
    }

    public FlightStatus getFlightStatus(){
        return flightStatus;
    }

    public void setFlightStatus( FlightStatus flightStatus){
        this.flightStatus = flightStatus;
    }

}
