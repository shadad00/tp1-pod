package ar.edu.itba.models;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Flight implements Serializable {

    private final String flightCode;
    private final String destiny;
    private FlightStatus status;
    private final EnumMap<SeatCategory, FlightTicketsMap> flightSeatsMap;
    private final ConcurrentHashMap<String, Ticket> tickets;
    private final String mutex_status = "Status";


    public Flight(String flightCode, String destiny, Plane plane, ConcurrentHashMap<String, Ticket> tickets) {
        this.flightCode = flightCode;
        this.destiny = destiny;
        this.status = FlightStatus.PENDING;

        this.flightSeatsMap = new EnumMap<>(SeatCategory.class);

        PlaneCategoryInformation categoryInformation;
        for (SeatCategory category : SeatCategory.values()) {
            if( (categoryInformation=plane.getCategoryDescription(category)) != null)
                flightSeatsMap.put(category, new FlightTicketsMap(categoryInformation));
        }

        this.tickets = tickets;
    }

    public String getFlightCode() {
        return flightCode;
    }


    public String getDestiny() {
        return destiny;
    }


    public FlightTicketsMap getFlightTicketsMapByCategory(SeatCategory category){
            return this.flightSeatsMap.get(category);
    }

    public SeatCategory getCategoryFromRow(int row){
        for (SeatCategory category : SeatCategory.values()) {
                if(flightSeatsMap.containsKey(category) && flightSeatsMap.get(category).containsRow(row))
                    return category;
        }
        return null;
    }


    public int getAvailableSeatsByCategory(SeatCategory category){
            if (this.flightSeatsMap.containsKey(category)) {
                return this.flightSeatsMap.get(category).getAvailableSeats();
            }
        return 0;
    }


    public FlightStatus getStatus() {
        synchronized (mutex_status) {
            return status;
        }
    }

    public void setStatus(FlightStatus status) {
        synchronized (mutex_status) {
            this.status = status;
        }
    }



    public String isSeatAvailable(Integer row, Integer col){
        for(FlightTicketsMap seats: flightSeatsMap.values()){
            if(seats.contains(row, col)) {
                return seats.getPassengerFromSeat(row, col);
            }
        }
        throw new IllegalArgumentException("Seat does not exist");
    }


    public void assignSeat(Ticket ticket, Integer row, Integer col) throws IllegalStateException{
        if(!this.getStatus().equals(FlightStatus.PENDING))
            throw new IllegalArgumentException("Cannot assign seat to a flight that is not pending");

        for(Map.Entry<SeatCategory, FlightTicketsMap> entry: flightSeatsMap.entrySet()) {
            if(entry.getValue().contains(row, col)) {
                if (entry.getKey().ordinal() >= ticket.getCategory().ordinal()) {
                    entry.getValue().assignSeat(row, col, ticket);
                    return;
                } else
                    throw new IllegalArgumentException("Cannot assign a seat of a higher category than the ticket");

            }
        }
        throw new IllegalArgumentException("Invalid seat");
    }

    public void freeSeatByPassenger(Ticket ticket){
        if(ticket.hasSeat()){
            flightSeatsMap.get( ticket.getSeat().getCategory() )
                    .freeSeat(ticket.getSeat().getRow(), ticket.getSeat().getColumn());
            ticket.clearSeat();
        }

    }

    public boolean passengerExists(String passenger){
        return tickets.containsKey(passenger);
    }

    public void deletePassengerTicket(String passenger){
        Ticket ticket = Optional.ofNullable(tickets.get(passenger))
                .orElseThrow(() -> new IllegalArgumentException("Passenger does not exist in this flight"));
        freeSeatByPassenger(ticket);
        tickets.remove(passenger);
    }

    public Collection<Ticket> getTickets() {
        return Collections.unmodifiableCollection(tickets.values());
    }


    public Ticket getTicket(String passenger){
        return this.tickets.get(passenger);
    }

    public void addTicket(Ticket ticket){
        if(tickets.containsKey(ticket.getPassenger()))
            throw new IllegalArgumentException("Passenger already has a ticket for this flight");
        tickets.put(ticket.getPassenger(), ticket);
    }

}
