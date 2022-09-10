package ar.edu.itba.models;

import ar.edu.itba.models.utils.RowColumnPair;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String passenger;
    private SeatCategory category;
    private Seat seat;


    public SeatCategory getCategory() {
        return category;
    }

    public void setCategory(SeatCategory category) {
        this.category = category;
    }

    public boolean hasSeat(){
        return this.seat != null;
    }

    public String getPassenger() {
        return passenger;
    }

    public Seat getSeat() {
        return seat;
    }

    public void assignSeat(Seat seat){
        this.seat = seat;
    }

    public void clearSeat(){
        this.seat = null;
    }


}
