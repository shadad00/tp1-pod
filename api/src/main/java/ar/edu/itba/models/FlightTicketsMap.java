package ar.edu.itba.models;

import java.io.Serializable;
import java.util.Arrays;

public class FlightTicketsMap implements Serializable {

    private final PlaneCategoryInformation categoryInformation;
    private final Ticket[][] ticketSeats;

    public FlightTicketsMap(PlaneCategoryInformation description) {
        this.categoryInformation = description;
        this.ticketSeats = new Ticket[description.getFinalRow() - description.getInitialRow() + 1][description.getColumnsNumber()];
    }

    public Integer getInitialRow(){
        return categoryInformation.getInitialRow();
    }

    public Integer getRowsNumber() {
        return categoryInformation.getFinalRow() - categoryInformation.getInitialRow() + 1;
    }

    public Integer getColumnsNumber() {
        return categoryInformation.getColumnsNumber();
    }

    public Ticket[][] getTicketSeats() {
        return ticketSeats;
    }

    public Ticket[] getRow(Integer row){
        if(!categoryInformation.containsRow(row))
            throw new IllegalArgumentException("Row does not exists");
        return ticketSeats[getRealRowIndex(row)];
    }

    public boolean contains(Integer row, Integer col){
        return categoryInformation.contains(row, col);
    }

    public boolean containsRow(Integer row){
        return categoryInformation.containsRow(row);
    }

    public Integer getAvailableSeats(){
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
        if( !contains(row, col) )
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
            ticket.assignSeat(new Seat(row,col, categoryInformation.getCategory()));
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
        return row - categoryInformation.getInitialRow();
    }
    private int getRealRow(Integer row){
        return row + categoryInformation.getInitialRow();
    }

}
