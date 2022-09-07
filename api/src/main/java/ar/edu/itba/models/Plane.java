package ar.edu.itba.models;

import javafx.util.Pair;

import java.util.EnumMap;
import java.util.Map;

public class Plane {

    private final String modelName; //// @bruno Es necesario?
    private final EnumMap<SeatCategory, CategoryDescription> categoryRows;


    public Plane(String modelName) {
        this.modelName = modelName;
        this.categoryRows = new EnumMap<>(SeatCategory.class);
    }

    public boolean seatExists(int row, int col){
        return true;
    }

    public SeatCategory getCategoryFromSeat(int row, char col){
        //TODO depende de lo que hizo facu
        return null;
    }

}
