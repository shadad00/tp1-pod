package ar.edu.itba.models;

import java.io.Serializable;

public enum SeatCategory implements Serializable,Comparable<SeatCategory> {
    BUSINESS("BUSINESS"),
    PREMIUM_ECONOMY("PREMIUM_ECONOMY"),
    ECONOMY("ECONOMY");

    private final String name;

    SeatCategory (String name ){
        this.name = name;
    }

}
