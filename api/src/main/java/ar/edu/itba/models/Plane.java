package ar.edu.itba.models;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Objects;

public class Plane implements Serializable {

    private final String modelName;
    private final EnumMap<SeatCategory, PlaneCategoryInformation> categoryRows;


    public Plane(String modelName, EnumMap<SeatCategory, PlaneCategoryInformation> categoryRows) {
        this.modelName = modelName;
        this.categoryRows = categoryRows;
    }

    public PlaneCategoryInformation getCategoryDescription(SeatCategory category){
        return this.categoryRows.get(category);
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
