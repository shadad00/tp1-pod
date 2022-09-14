package ar.edu.itba.models;

import java.io.Serializable;

public class Ticket implements Serializable {
    private final String passenger;
    private SeatCategory category;
    private Seat seat;

    public Ticket(String passenger, SeatCategory category) {
        this.passenger = passenger;
        this.category = category;
    }

    public synchronized SeatCategory getCategory() {
        return category;
    }

    public synchronized void setCategory(SeatCategory category) {
        this.category = category;
    }

    public synchronized boolean hasSeat(){
            return this.seat != null;

    }

    public synchronized Seat getSeat() {
            return seat;

    }

    public synchronized void assignSeat(Seat seat){
            this.seat =seat;
    }


    public void clearSeat(){
        assignSeat(null);
    }

    public String getPassenger() {
        return passenger;
    }


}
