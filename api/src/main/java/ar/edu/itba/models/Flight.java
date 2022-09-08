package ar.edu.itba.models;

import java.util.EnumMap;
import java.util.List;

public class Flight {
    private final String flightCode;
    private final String destiny;
    private final Plane plane;

    private final EnumMap<SeatCategory, Integer> availableSeats;

    private FlightStatus status;

    private final List<Ticket> tickets;

    public Flight(String flightCode, String destiny, Plane plane, List<Ticket> tickets) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.plane = plane;

        this.availableSeats = new EnumMap<>(SeatCategory.class);
        CategoryDescription description;
        for (SeatCategory category : SeatCategory.values()) {
            description = plane.getCategoryDescription(category);
            if(description!=null){
                this.availableSeats.put(category, description.getTotalSeats());
            }
        }

        this.tickets = tickets;
    }


    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public String getDestiny() {
        return destiny;
    }

    public boolean hasAvailableSeats(SeatCategory category){
        for (int i = category.ordinal(); i < SeatCategory.values().length; i++) {
            if(availableSeats.get(SeatCategory.values()[i]) > 0 )
                return true;
        }
        return false;
    }

    public Integer getBestAvailableCategory(SeatCategory category){
        SeatCategory[] categories = SeatCategory.values();
        for (int i = category.ordinal(); i < SeatCategory.values().length; i++) {
            if(availableSeats.get(categories[i]) > 0 )
                return i;
        }
        return -1;
    }

    public int getAvailableSeats(){
        return availableSeats.values().stream().reduce(0, Integer::sum);
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void addTicket(Ticket ticket){
        tickets.add(ticket);
    }
}
