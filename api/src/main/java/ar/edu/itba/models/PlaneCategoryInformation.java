package ar.edu.itba.models;

import java.io.Serializable;

public class PlaneCategoryInformation implements Serializable {
    private final Integer initialRow;
    private final Integer finalRow;
    private final Integer columnsNumber;
    private final SeatCategory category;

    public PlaneCategoryInformation(SeatCategory category, Integer fromRow, Integer toRow, Integer columnsNumber) {
        this.category = category;
        this.initialRow = fromRow;
        this.finalRow = toRow;
        this.columnsNumber = columnsNumber;
    }

    public int getInitialRow() {
        return initialRow;
    }

    public int getFinalRow() {
        return finalRow;
    }

    public int getColumnsNumber(){
        return columnsNumber;
    }

    public SeatCategory getCategory() {
        return category;
    }

    public boolean contains(Integer row, Integer column){
        return containsRow(row) && containsColumn(column);
    }

    public boolean containsRow(Integer row){
        if(row == null)
            throw new IllegalArgumentException();
        return row >= initialRow && row <= finalRow;
    }

    public boolean containsColumn(Integer column){
        if(column == null)
            throw new IllegalArgumentException();
        return column >= 0 && column < columnsNumber;
    }

    public int getTotalSeats(){
        return (finalRow - initialRow + 1) * columnsNumber;
    }

}
