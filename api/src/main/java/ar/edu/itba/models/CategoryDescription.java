package ar.edu.itba.models;

import java.io.Serializable;

public class CategoryDescription implements Serializable {
    private final Integer fromRow;
    private final Integer toRow;
    private final Integer columnsNumber;
    private final SeatCategory category;

    public CategoryDescription(SeatCategory category, Integer fromRow, Integer toRow, Integer columnsNumber) {
        this.category = category;
        this.fromRow = fromRow;
        this.toRow = toRow;
        this.columnsNumber = columnsNumber;
    }

    public int getFromRow() {
        return fromRow;
    }

    public int getToRow() {
        return toRow;
    }

    public int getColumnsNumber(){
        return columnsNumber;
    }

    public SeatCategory getCategory() {
        return category;
    }

    public boolean contains(Integer row, Integer column){
        return row >= fromRow && row <= toRow && column > 0 && column < columnsNumber;
    }

    public boolean containsRow(Integer row){
        return row >= fromRow && row <= toRow;
    }

    public boolean containsColumn(Integer column){
        return column > 0 && column < columnsNumber;
    }

    public int getTotalSeats(){
        return (toRow - fromRow + 1) * columnsNumber;
    }

}
