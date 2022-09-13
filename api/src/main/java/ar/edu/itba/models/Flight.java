package ar.edu.itba.models;

import java.io.Serializable;
import java.util.*;

public class Flight implements Serializable {

    private final String flightCode;
    private final String destiny;
    private FlightStatus status;
    private final EnumMap<SeatCategory, FlightTicketsMap> flightSeatsMap;
    private final Map<String, Ticket> tickets;

    public Flight(String flightCode, String destiny, Plane plane, Map<String, Ticket> tickets) {
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

    public FlightTicketsMap getFlightTicketsMapByCategory(SeatCategory category){
        return this.flightSeatsMap.get(category);
    }

    public SeatCategory getCategoryFromRow(int row){
        for (SeatCategory category : SeatCategory.values()) {
            if(flightSeatsMap.get(category) != null && flightSeatsMap.get(category).containsRow(row))
                return category;
        }
        return null;
    }


    public int getAvailableSeatsByCategory(SeatCategory category){
        if (this.flightSeatsMap.get(category) != null) {
            return this.flightSeatsMap.get(category).getAvailableSeats();
        }
        return 0;
    }


    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public Collection<Ticket> getTickets() {
        return tickets.values();
    }

    public String getDestiny() {
        return destiny;
    }

    public boolean hasAvailableSeatsForCategoryOrLower(SeatCategory category){
        for (int i = category.ordinal(); i < SeatCategory.values().length; i++) {
            if(flightSeatsMap.get(SeatCategory.values()[i]) != null && flightSeatsMap.get(SeatCategory.values()[i]).getAvailableSeats() > 0 )
                return true;
        }
        return false;
    }

    public SeatCategory getBestAvailableCategory(SeatCategory category){
        for(SeatCategory categories : SeatCategory.values())
            if(categories.compareTo(category) <= 0 && flightSeatsMap.get(categories).getAvailableSeats() > 0)
                return categories;
        return SeatCategory.ECONOMY;
    }

    public String isSeatAvailable(Integer row, Integer col){
        for(FlightTicketsMap seats: flightSeatsMap.values()){
            if(seats.contains(row, col)) {
                return seats.getPassengerFromSeat(row, col);
            }
        }
        throw new IllegalArgumentException("Seat does not exist");
    }

    public int getAvailableSeats(){
        return flightSeatsMap.values().stream().mapToInt(FlightTicketsMap::getAvailableSeats).reduce(0, Integer::sum);
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void addTicket(Ticket ticket){
        if(tickets.containsKey(ticket.getPassenger()))
            throw new IllegalArgumentException();
        tickets.put(ticket.getPassenger(), ticket);
    }

    public boolean assignSeat(String passenger, Integer row, Integer col){
        Ticket ticket;
        if(status != FlightStatus.PENDING
            || (ticket=Optional.ofNullable(tickets.get(passenger))
                .orElseThrow(IllegalArgumentException::new))
                .getSeat() != null
        )
            return false; //para asignar un asiento solo debe estar pendiente el vuelo


        for(Map.Entry<SeatCategory, FlightTicketsMap> entry: flightSeatsMap.entrySet()) {
            // si el asiento es de menor o igual categoria que la del ticket comprado
            if (/*entry.getKey().compareTo(ticket.getCategory())>=0*/ entry.getKey().ordinal() >= ticket.getCategory().ordinal() && entry.getValue().contains(row, col)) {
                return entry.getValue().assignSeat(row, col, ticket) != null;
            }
        }
        return false;
    }

    public void freeSeatByPassenger(String passengerName){
        Ticket ticket = Optional.ofNullable(tickets.get(passengerName))
                .orElseThrow(IllegalArgumentException::new);
        if(ticket.hasSeat()){
            flightSeatsMap.get( ticket.getSeat().getCategory() )
                    .freeSeat(ticket.getSeat().getRow(), ticket.getSeat().getColumn());
            ticket.clearSeat();
        }

    }

    public boolean passengerExists(String passenger){
        return tickets.containsKey(passenger);
    }

    public Ticket deletePassengerTicket(String passenger){
        Ticket ticket = Optional.ofNullable(tickets.get(passenger))
                .orElseThrow(IllegalArgumentException::new);
        freeSeatByPassenger(passenger);
        tickets.remove(passenger);
        return ticket;
    }

    public boolean seatExists(Integer row, Integer column){
        for(FlightTicketsMap seats: flightSeatsMap.values()){
            if(seats.contains(row, column))
                return true;
        }
        return false;
    }

    public Ticket getTicket(String passenger){
        return this.tickets.get(passenger);
    }


}
