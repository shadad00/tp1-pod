package ar.edu.itba.models;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Objects;

public class Plane implements Serializable {

    private final String modelName;
    private final EnumMap<SeatCategory, CategoryDescription> categoryRows;
    private final Integer seats;



    public Plane(String modelName, EnumMap<SeatCategory, CategoryDescription> categoryRows) {
        this.modelName = modelName;
        this.categoryRows = categoryRows;
        this.seats = categoryRows.values().stream().mapToInt(CategoryDescription::getTotalSeats).reduce(0, Integer::sum);
    }

    public CategoryDescription getCategoryDescription(SeatCategory category){
        return this.categoryRows.get(category);
    }

    public boolean seatExists(int row, int col){
        int totalRows;
        for (SeatCategory category : SeatCategory.values()) {
            if(categoryRows.get(category).contains(row, col))
                return true;
        }
        return false;
    }

    public String getModelName(){
        return modelName;
    }

    public Integer getSeats() {
        return seats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plane plane = (Plane) o;
        return modelName.equals(plane.modelName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelName);
    }
}
