package ar.edu.itba.models.utils;

import java.io.Serializable;

public class RowColumnPair implements Serializable {
    private Integer row;
    private Integer column;

    public RowColumnPair(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }
}
