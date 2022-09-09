package ar.edu.itba.models;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.*;

public class Flight implements Serializable {
    private final String flightCode;
    private final String destiny;


//    private final EnumMap<SeatCategory, Integer> availableSeats;

    private final EnumMap<SeatCategory, CategorySeats> categorySeats;



    private FlightStatus status;

    private final Map<String, Ticket> tickets;

    public Flight(String flightCode, String destiny, Plane plane, Map<String, Ticket> tickets) {
        this.flightCode = flightCode;
        this.destiny = destiny;



        this.categorySeats = new EnumMap<>(SeatCategory.class);
        for (SeatCategory value : SeatCategory.values()) {
            this.categorySeats.put(value, new CategorySeats(plane.getCategoryDescription(value)));
        }

        this.tickets = tickets;
    }


    public FlightStatus getStatus() {
        return status;
    }
    public FlightStatus getFlightStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Collection<Ticket> getTickets() {
        return tickets.values();
    }

    public String getDestiny() {
        return destiny;
    }

    public boolean hasAvailableSeatsForCategoryOrLower(SeatCategory category){
        for (int i = category.ordinal(); i < SeatCategory.values().length; i++) {
            if(categorySeats.get(SeatCategory.values()[i]).getAvailableSeats() > 0 )
                return true;
        }
        return false;
    }

    public SeatCategory getBestAvailableCategory(SeatCategory category){
        SeatCategory[] categories = SeatCategory.values();
        for (int i = category.ordinal(); i < SeatCategory.values().length; i++) {
            if(categorySeats.get(categories[i]).getAvailableSeats() > 0 )
                return SeatCategory.values()[i];
        }
        return null;
    }

    public boolean isSeatAvailable(Integer row, Integer col){
        for(CategorySeats seats: categorySeats.values()){
            if(seats.contains(row, col))
                return seats.isSeatAvailable(row, col);
        }
        return false;
    }

    public int getAvailableSeats(){
        return categorySeats.values().stream().mapToInt(CategorySeats::getAvailableSeats).reduce(0, Integer::sum);
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void addTicket(Ticket ticket){
        if(tickets.containsKey(ticket.getPassenger()))
            throw new IllegalArgumentException();
        tickets.put(ticket.getPassenger(), ticket);
    }

    public void assignSeat(String passenger, Integer row, Integer col){
        if(status != FlightStatus.PENDING)
            return; //para asignar un asiento solo debe estar pendiente el vuelo
        Ticket ticket;
        if((ticket=tickets.getOrDefault(passenger, new Ticket())).getSeat() != null)
            return; //el pasajero ya tiene un asiento en el vuelo


        Seat seat = null;
        for(Map.Entry<SeatCategory, CategorySeats> entry: categorySeats.entrySet()) {

            if (entry.getKey().ordinal() <= ticket.getCategory().ordinal())
                seat = entry.getValue().assignSeat(row, col);
                if (seat != null) {
                    ticket.assignSeat(seat);
                    return; // ya se asigno el asiento
                }

        }

//        for (int i = ticket.getCategory().ordinal(); i < SeatCategory.values().length; i++) {
//            seat = categorySeats.get(SeatCategory.values()[i]).assignSeat(row, col);
//            if (seat != null) {
//                ticket.assignSeat(seat);
//                return; // ya se asigno el asiento
//            }
//        }

    }

    public void freePassengerSeat(String passengerName){
        Ticket ticket = tickets.get(passengerName);

        if(ticket.hasSeat()){
            categorySeats.get(ticket.getCategory()).freeSeat(ticket.getSeat().getRow(), ticket.getSeat().getColumn());
        }

    }

    public boolean seatExists(Integer row, Integer column){
        for(CategorySeats seats: categorySeats.values()){
            if(seats.contains(row, column))
                return true;
        }
        return false;
    }


}
