package ar.edu.itba.models;

import ar.edu.itba.remoteInterfaces.FlightAdministration;

import java.rmi.RemoteException;
import java.util.*;

public class Airline implements FlightAdministration {

    private final Map<String, List<Flight>> flights;
    private final Map<String, Plane> models;

    public Airline() {
        this.flights = new HashMap<>();
        this.models = new HashMap<>();
    }





    @Override
    public void addPlaneModel(String modelName, int economyRows, int economyCols,
                              int premiumEconomyRows, int premiumEconomyCols,
                              int businessRows, int businessCols) throws RemoteException {



        if (models.putIfAbsent(plane.getModelName(), plane) == null){
            throw new RuntimeException();
        }




    }


    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, List<Seat> seatList) throws RemoteException {
        Plane plane = this.models.get(modelName);
        EnumMap<SeatCategory, Seat[][]> planeInformation= new EnumMap<>(SeatCategory.class);
        for(SeatCategory category : SeatCategory.values()){
            CategoryDescription categoryDescription = plane.getCategoryDescription(category);
            planeInformation.put(category , new Seat[categoryDescription.getFromRow() - categoryDescription.getToRow()][categoryDescription.getColumnsNumber()]);
        }
        Flight newFlight = new Flight(modelName,destinationAirportCode,plane,planeInformation);
        flights.putIfAbsent(modelName,new ArrayList<>());
        flights.get(modelName).add(newFlight);
    }

    @Override
    public FlightStatus getFlightStatus(String flightCode) throws RemoteException {
        return flights.get(flightCode);
    }

    @Override
    public void confirmFlight(String flightCode) throws RemoteException {

    }

    @Override
    public void cancelFlight(String flightCode) throws RemoteException {

    }

    @Override
    public void forceTicketChangeForCancelledFlights() throws RemoteException {

    }
}
