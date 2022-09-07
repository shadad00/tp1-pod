package ar.edu.itba.models;

public class Seat {
    private final int row, column;
    private final SeatCategory category ;
    private final Passenger passenger;

    public Seat(int row, int column, SeatCategory category, Passenger passenger) {
        this.row = row;
        this.column = column;
        this.category = category;
        this.passenger = passenger;
    }

    @Override
    public String toString() {
        return  (row+1) + " " + ('A' + column) + " "
                + ((passenger!=null)?passenger:"*") + " " +
                category;
    }

    public boolean isFree(){
        return (passenger==null);
    }
}
