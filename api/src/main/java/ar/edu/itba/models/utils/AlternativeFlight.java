package ar.edu.itba.models.utils;

import ar.edu.itba.models.SeatCategory;

public class AlternativeFlight implements Comparable<AlternativeFlight> {
    private final Integer seats;
    private final SeatCategory category;
    private final String flightCode;
    private final String destiny;

    public AlternativeFlight(Integer seats, SeatCategory category, String flightCode, String destiny) {
        this.seats = seats;
        this.category = category;
        this.flightCode = flightCode;
        this.destiny = destiny;
    }




    @Override
    public int compareTo(AlternativeFlight o) {
        if (o == null) {
            throw new IllegalArgumentException("Flight null");
        }
        int result;
        if((result = this.category.compareTo(o.category)) == 0){
            if( (result = o.seats.compareTo(this.seats)) == 0)
                result = this.flightCode.compareTo(o.flightCode);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %d %s\n", destiny, flightCode, seats, category);
    }

    public Integer getSeats() {
        return seats;
    }

    public SeatCategory getCategory() {
        return category;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getDestiny() {
        return destiny;
    }
}
