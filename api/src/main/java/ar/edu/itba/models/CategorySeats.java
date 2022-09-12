package ar.edu.itba.models;

import java.io.Serializable;
import java.util.Arrays;

public class CategorySeats implements Serializable {

    private final CategoryDescription description;
    private final Ticket[][] ticketSeats;

    public CategorySeats(CategoryDescription description) {
        this.description = description;
        this.ticketSeats = new Ticket[description.getToRow() - description.getFromRow() + 1][description.getColumnsNumber()];
    }

    public Integer getFromRow(){
        return description.getFromRow();
    }

    public Integer getNrows() {
        return description.getToRow() - description.getFromRow() + 1;
    }

    public Integer getNcols() {
        return description.getColumnsNumber();
    }

    public Ticket[][] getTicketSeats() {
        return ticketSeats;
    }

    public Ticket[] getRow(Integer row){
        if(!description.containsRow(row))
            throw new IllegalArgumentException("Row does not exists");
        return ticketSeats[getRealRowIndex(row)];
    }

    public boolean contains(Integer row, Integer col){
        return description.contains(row, col);
    }

    public boolean containsRow(Integer row){
        return description.containsRow(row);
    }

    public Integer getAvailableSeats(){
//        int result = 0;
//        for (Ticket[] row: ticketSeats
//             ) {
//            for (Ticket ticket : row
//                 ) {
//                result += ticket==null? 1 : 0;
//            }
//        }
//        return result;
        return Arrays.stream(ticketSeats).mapToInt(row -> Arrays.stream(row).
                mapToInt(seat -> seat == null? 1 : 0).reduce(0, Integer::sum)).
                reduce(0, Integer::sum);
    }

    public boolean isSeatAvailable(Integer row, Integer col){
        if(!contains(row, col))
            throw new IllegalArgumentException();
        return ticketSeats[getRealRowIndex(row)][col]==null;
    }

    public String getPassengerFromSeat(Integer row, Integer col){
        if(!contains(row, col))
            throw new IllegalArgumentException();
        return ticketSeats[getRealRowIndex(row)][col]==null ?
                null : ticketSeats[getRealRowIndex(row)][col].getPassenger();
    }


    public Ticket assignSeat(Integer row, Integer col,Ticket ticket){
        if(!contains(row, col))
            throw new IllegalArgumentException();

        // trato de asignarle el asiento pedido
        if (isSeatAvailable(row, col)) {
            int newRow = getRealRowIndex(row);
            ticket.assignSeat(new Seat(row,col,description.getCategory()));
            ticketSeats[newRow][col] = ticket;
            return ticketSeats[newRow][col];
        }

        // trato de asignarle un asiento libre de esta categoria
        /*for (int i = 0; i < ticketSeats.length; i++) {
            for (int j = 0; j < ticketSeats[0].length; j++) {
                if (isSeatAvailable(i, j)) {
                    ticket.assignSeat(new Seat(i,j,ticket.getCategory()));
                    ticketSeats[i][j] = ticket;
                    return ticketSeats[i][j];
                }

            }

        }*/
        return null;
    }

    public void freeSeat(Integer row, Integer column){
        if(!contains(row, column))
            throw new IllegalArgumentException();

        ticketSeats[getRealRowIndex(row)][column] = null;
    }

    private int getRealRowIndex(Integer row){
        return row - description.getFromRow();
    }

    private int getRealRow(Integer row){
        return row + description.getFromRow();
    }

}
