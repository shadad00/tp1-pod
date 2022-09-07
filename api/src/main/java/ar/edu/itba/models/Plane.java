package ar.edu.itba.models;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Plane {

    private final String modelName;
    private final EnumMap<SeatCategory, CategoryDescription> categoryRows;


    public Plane(String modelName) {
        this.modelName = modelName;
        this.categoryRows = new EnumMap<>(SeatCategory.class);
    }

    public void addCategory(SeatCategory category , int rows , int columns){
        if(categoryRows.containsKey(category))
            return ; // throw Exception
        int fromRow=0;
        for (Map.Entry<SeatCategory, CategoryDescription> entry : categoryRows.entrySet()){
            fromRow =  entry.getValue().getToRow() + 1 ;
        }
        categoryRows.put(category,new CategoryDescription(fromRow,fromRow+rows,columns));
    }

    public CategoryDescription getCategoryDescription(SeatCategory category){
        return this.categoryRows.get(category);
    }

    public boolean seatExists(int row, int col){

    }

    public String getModelName(){
        return modelName;
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
        return Objects.hash(modelName, categoryRows);
    }
}
