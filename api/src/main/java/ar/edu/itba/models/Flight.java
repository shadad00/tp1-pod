package ar.edu.itba.models;

import java.rmi.RemoteException;
import java.util.*;
import java.util.EnumMap;

public class Flight implements Comparable<Flight>{
    private final String flightCode;
    private final String destiny;
    private final Plane plane;
//    private final EnumMap<SeatCategory, Seat[][]> seats;
    //lock
    private FlightStatus status = FlightStatus.PENDING;;

    private final int freeSeats;
    private final int[] freeSeatsCategories;

    private Map<Integer, Map<Character, Passenger>> seatsByRowCol = new HashMap<>();
//    private final Map<Passenger, Seat> passengersSeats = new HashMap<>();

    private final List<Passenger> passengers;

    public Flight(String flightCode, String destiny, Plane plane, List<Passenger> passengers) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.plane = plane;
        this.passengers = passengers;


    }

    public Seat getSeat(int row, char column){

        Map<Character, Passenger> aux = seatsByRowCol.get(row);
        if (aux != null) {
            return aux.get(column);
        }

        return null;
    }

    public void addPassengerSeat(Passenger p, int row, char column){
        seatsByRowCol.computeIfAbsent(row, k -> new HashMap<Character, Passenger>());
        seatsByRowCol.get(row).putIfAbsent(column, p);
//        passengersSeats.put(p, new Seat(row, column, p.getCategory(), p ));
        //TODO habria que agregarlo a seatsByRowCol cuando este definido como los vamos a guardar
    }

//    public void addPassengers(List<Passenger> p){
//        passengers = p;
//    }

    public void removePassenger(Passenger name){
        Seat seat = passengersSeats.get(name);
        if (seat != null) {

        }
        passengersSeats.remove(name);
    }

    public Passenger getPassenger(String name){
        for (Passenger passenger : passengers) {
            if(passenger.getName().equals(name))
                return passenger;
        }
        return null;
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

//    public EnumMap<SeatCategory, Seat[][]> getSeats() {
//        return seats;
//    }

    public FlightStatus getFlightStatus(){
        return status;
    }

    public void setFlightStatus( FlightStatus flightStatus){
        this.status = flightStatus;
    }

    @Override
    public int compareTo(Flight o) {
        // se prefieren los asientos de mejor categoria
        //sino el vuelo menos completo
        //En caso de igualdad el de menor codigo alfabetico
        return 0;
    }

}
