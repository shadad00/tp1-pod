package ar.edu.itba.models.utils;

import java.io.Serializable;

public class RowColumnPair implements Serializable {
    private final Integer row;
    private final Integer column;

    public RowColumnPair(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    public Integer getRow() {
        return row;
    }


    public Integer getColumn() {
        return column;
    }

}
