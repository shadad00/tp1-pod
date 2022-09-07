package flightService.servant;

import ar.edu.itba.models.*;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.*;

public class FlightAdministrationImpl implements FlightAdministration {

    private static final int MAX_COLS = 25;
    private static final int MIN_COLS = 0;
    private static final int MIN_ROWS = 0;

    private FlightCentral flightCentral;

    public FlightAdministrationImpl(FlightCentral flightCentral){
        this.flightCentral=flightCentral;
    }


    @Override
    public void addPlaneModel(String modelName, EnumMap<SeatCategory, Pair<Integer>> categorySeats)
            throws RemoteException {

        if(categorySeats == null)
            throw new RuntimeException();

        for(Pair<Integer> pair : categorySeats.values()){
            if(pair.getX() <= MIN_ROWS || pair.getY() <= MIN_COLS || pair.getY() > MAX_COLS)
                throw new RemoteException();
        }

        Plane plane= new Plane(modelName);

        Pair<Integer> categoryDimension= categorySeats.get(SeatCategory.BUSINESS);
        Optional.ofNullable(categoryDimension).ifPresent( x ->
                plane.addCategory(SeatCategory.BUSINESS, x.getX(), x.getY()));

        categoryDimension= categorySeats.get(SeatCategory.PREMIUM_ECONOMIC);
        Optional.ofNullable(categoryDimension).ifPresent( x ->
                plane.addCategory(SeatCategory.PREMIUM_ECONOMIC, x.getX(), x.getY()));

        categoryDimension= categorySeats.get(SeatCategory.ECONOMIC);
        Optional.ofNullable(categoryDimension).ifPresent( x ->
                plane.addCategory(SeatCategory.ECONOMIC, x.getX(), x.getY()));


        if(flightCentral.addModel(modelName, plane) == null)
            throw new RuntimeException();


    }


    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, List<Passenger> passengers) throws RemoteException {
        Plane plane = Optional.ofNullable(flightCentral.getModels(modelName)).orElseThrow(RemoteException::new);    // TODO add custom exception

//        EnumMap<SeatCategory, Seat[][]> planeInformation= new EnumMap<>(SeatCategory.class);

//        for(SeatCategory category : SeatCategory.values()){
//            CategoryDescription categoryDescription = plane.getCategoryDescription(category);
//            planeInformation.put(category , new Seat[categoryDescription.getToRow() - categoryDescription.getFromRow()][categoryDescription.getColumnsNumber()]);
//        }

        Flight newFlight = new Flight(modelName,destinationAirportCode,plane, passengers);

        flightCentral.addFlight(modelName, newFlight);
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return flightCentral.getFlight(flightCode).getFlightStatus();
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {
        flightCentral.getFlight(flightCode).setFlightStatus(FlightStatus.CONFIRMED);
    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {
        flightCentral.getFlight(flightCode).setFlightStatus(FlightStatus.CANCELLED);
    }

    @Override
    public void forceTicketChangeForCancelledFlights() throws RemoteException {
        List<Flight> suspended = this.flightCentral.getSuspendedFlights();
        for(Flight flight : suspended){
            SortedSet<Flight> alternatives = this.flightCentral.getAlternativeFlights(flight.getDestiny(),);


        }


    }
}
