package flightService.servant;

import ar.edu.itba.models.*;
import ar.edu.itba.remoteInterfaces.FlightAdministration;
import flightService.server.FlightCentral;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class FlightAdministrationImpl implements FlightAdministration {

    private FlightCentral flightCentral;

    public FlightAdministrationImpl(FlightCentral flightCentral){
        this.flightCentral=flightCentral;
    }

    @Override
    public void addPlaneModel(String modelName, EnumMap<SeatCategory, Pair<Integer>>) throws RemoteException {





        if (models.putIfAbsent(plane.getModelName(), plane) == null){
            throw new RuntimeException();
        }




    }


    @Override
    public void addFlight(String modelName, String flightCode, String destinationAirportCode, List<Seat> seatList) throws RemoteException {
        Plane plane = this.flightCentral.getModels(modelName);
        EnumMap<SeatCategory, Seat[][]> planeInformation= new EnumMap<>(SeatCategory.class);
        for(SeatCategory category : SeatCategory.values()){
            CategoryDescription categoryDescription = plane.getCategoryDescription(category);
            planeInformation.put(category , new Seat[categoryDescription.getToRow() - categoryDescription.getFromRow()][categoryDescription.getColumnsNumber()]);
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
