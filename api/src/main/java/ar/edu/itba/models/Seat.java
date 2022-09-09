package ar.edu.itba.models;

import java.io.Serializable;

public class Seat implements Serializable {
    private final int row, column;
    private final SeatCategory category ;

    public Seat(int row, int column, SeatCategory category) {
        this.row = row;
        this.column = column;
        this.category = category;
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

    @Override
    public String toString() {
        return  (row+1) + " " + ('A' + column) + " " +
                category;
    }
}
