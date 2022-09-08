package ar.edu.itba.models;

public class CategoryDescription {
    private final int fromRow;
    private final int toRow;
    private final int columnsNumber;

    public CategoryDescription(int fromRow, int toRow, int columnsNumber) {
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

    public boolean contains(int row, int column){
        return row >= fromRow && row <= toRow && column > 0 && column < columnsNumber;
    }

    public int getTotalSeats(){
        return (toRow - fromRow + 1) * columnsNumber;
    }

}
