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

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public SeatCategory getCategory() {
        return category;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public boolean belongsPassenger(Passenger passenger){
        return this.passenger.equals(passenger);
    }


    @Override
    public String toString() {
        return  (row+1) + " " + ('A' + column) + " "
                + ((passenger!=null)?passenger:"*") + " " +
                category;
    }
}
