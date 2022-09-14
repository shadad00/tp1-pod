package ar.edu.itba.models;

import java.io.Serializable;
import java.util.Comparator;

public enum SeatCategory implements Serializable{
    BUSINESS("BUSINESS"),
    PREMIUM_ECONOMY("PREMIUM_ECONOMY"),
    ECONOMY("ECONOMY");

    private final String name;

    public String getName() {
        return name;
    }

    SeatCategory (String name ){
        this.name = name;
    }

}
