package ar.edu.itba.models;

import java.util.EnumMap;
import java.util.Map;

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

}
