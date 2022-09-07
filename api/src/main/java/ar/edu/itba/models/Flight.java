package ar.edu.itba.models;

import java.rmi.RemoteException;
import java.util.*;
import java.util.EnumMap;

public class Flight {
    private final String flightCode;
    private final String destiny;
    private final Plane plane;
    private final EnumMap<SeatCategory, Seat[][]> seats;
    private FlightStatus status = FlightStatus.PENDING;;

    private Map<Integer, Map<Character, Seat>> asientos = new HashMap<>();

    private final Map<Passenger, Seat> passengers = new HashMap<>();




    public Flight(String flightCode, String destiny, Plane plane, EnumMap<SeatCategory, Seat[][]> seats) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.plane = plane;
        this.seats = seats;

    }

    public Seat getSeat(int row, char column){

        Map<Character, Seat> aux = asientos.get(row);
        if (aux != null) {
            return aux.get(column);
        }
        return null;
    }

    public void addPassenger(Passenger p, int row, char column){

        passengers.put(p, new Seat(row, column, null, p ));
        //TODO habria que agregarlo a asientos pero no se si esta bien esa columna
    }

    public void removePassenger(Passenger name){
        Seat seat = passengers.get(name);
        if (seat != null) {

        }
        passengers.remove(name);
    }

    public Passenger getPassenger(String name){

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
        return status;
    }

    public void setFlightStatus( FlightStatus flightStatus){
        this.status = flightStatus;
    }

}
