package ar.edu.itba.models;

import ar.edu.itba.models.utils.RowColumnPair;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;

public class CategorySeats {

    private final CategoryDescription description;
    private final Seat[][] seats;

    public CategorySeats(CategoryDescription description) {
        this.description = description;
        this.seats = new Seat[description.getToRow() - description.getFromRow() + 1][description.getColumnsNumber()];
    }

    public Integer getNrows() {
        return description.getToRow() - description.getFromRow() + 1;
    }

    public Integer getNcols() {
        return description.getColumnsNumber();
    }

    public Seat[][] getSeats() {
        return seats;
    }

    public Seat[] getRow(Integer row){
        if(!description.containsRow(row))
            throw new IllegalArgumentException("Row does not exists");
        return seats[getRealRowIndex(row)];
    }

    public boolean contains(Integer row, Integer col){
        return description.contains(row, col);
    }

    public Integer getAvailableSeats(){
        return Arrays.stream(seats).mapToInt(row -> Arrays.stream(row).mapToInt(seat -> seat == null? 0 : 1).reduce(0, Integer::sum)).reduce(0, Integer::sum);
    }

    public boolean isSeatAvailable(Integer row, Integer col){
        if(!contains(row, col))
            throw new IllegalArgumentException();

        return seats[getRealRowIndex(row)][col]==null;
    }

    public Seat assignSeat(Integer row, Integer col){
        if(!contains(row, col))
            throw new IllegalArgumentException();

        // trato de asignarle el asiento pedido
        if (isSeatAvailable(row, col)) {
            Integer newRow = getRealRowIndex(row);
            seats[newRow][col] = new Seat(row, col, description.getCategory());
            return seats[newRow][col];
        }

        // trato de asignarle un asiento libre de esta categoria
        for (int i = 0; i < seats.length; i++) {
            for (int j = 0; j < seats[0].length; j++) {
                if (isSeatAvailable(i, j)) {
                    seats[i][j] = new Seat(getRealRow(i), j, description.getCategory());
                    return seats[i][j];
                }

            }

        }
        return null;
    }

    public void freeSeat(Integer row, Integer column){
        if(!contains(row, column))
            throw new IllegalArgumentException();

        seats[getRealRowIndex(row)][column] = null;
    }

    private int getRealRowIndex(Integer row){
        return row - description.getFromRow();
    }

    private int getRealRow(Integer row){
        return row + description.getFromRow();
    }

}
