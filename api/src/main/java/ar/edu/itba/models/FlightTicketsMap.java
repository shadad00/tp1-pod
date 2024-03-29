package ar.edu.itba.models;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public class FlightTicketsMap implements Serializable {

    private final PlaneCategoryInformation categoryInformation;
    private final Ticket[][] ticketSeats;
    private final String mutex = "Ticket_semaphore";

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


    public Ticket[] getRow(Integer row){
        if(!categoryInformation.containsRow(row))
            throw new IllegalArgumentException("Row does not exists");
        synchronized (mutex) {
            return ticketSeats[getRealRowIndex(row)];
        }
    }

    public boolean contains(Integer row, Integer col){
        return categoryInformation.contains(row, col);
    }

    public boolean containsRow(Integer row){
        return categoryInformation.containsRow(row);
    }

    public Integer getAvailableSeats(){
        Stream<Ticket[]> stream;
        synchronized (mutex) {
             stream = Arrays.stream(ticketSeats);
        }
        return stream.mapToInt(row -> Arrays.stream(row).
                mapToInt(seat -> seat == null ? 1 : 0).reduce(0, Integer::sum)).
                reduce(0, Integer::sum);
    }

    public boolean isSeatAvailable(Integer row, Integer col){
        if(!contains(row, col))
            throw new IllegalArgumentException();
        synchronized (mutex){
            return ticketSeats[getRealRowIndex(row)][col]==null;
        }
    }

    public String getPassengerFromSeat(Integer row, Integer col){
        if( !contains(row, col) )
            throw new IllegalArgumentException();
       synchronized (mutex){
           return ticketSeats[getRealRowIndex(row)][col]==null ?
                   null : ticketSeats[getRealRowIndex(row)][col].getPassenger();
       }
    }


    public void assignSeat(Integer row, Integer col, Ticket ticket) throws IllegalStateException{

        if (isSeatAvailable(row, col)) {
            int newRow = getRealRowIndex(row);
            ticket.assignSeat(new Seat(row,col, categoryInformation.getCategory()));
           synchronized (mutex){
               ticketSeats[newRow][col] = ticket;
               return;
           }
        }


        throw new IllegalStateException("Seat is not available");
    }

    public void freeSeat(Integer row, Integer column){
        if(!contains(row, column))
            throw new IllegalArgumentException();

        synchronized (mutex){
            ticketSeats[getRealRowIndex(row)][column] = null;
        }
    }

    private int getRealRowIndex(Integer row){
        return row - categoryInformation.getInitialRow();
    }

}
