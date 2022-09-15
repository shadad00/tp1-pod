package flightService.server;

import ar.edu.itba.remoteInterfaces.FlightAdministration;
import ar.edu.itba.remoteInterfaces.FlightNotification;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import ar.edu.itba.remoteInterfaces.SeatMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import servant.FlightAdministrationImpl;
import servant.FlightNotificationImpl;
import servant.SeatAssignationImpl;
import servant.SeatMapImpl;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException  {
        logger.info("flightService Server Starting ...");
        FlightCentral flightCentral = new FlightCentral();
        FlightNotificationImpl flightNotification = new FlightNotificationImpl(flightCentral);
        flightCentral.setNotificator(flightNotification);
        FlightAdministration flightAdministration = new FlightAdministrationImpl(flightCentral);
        SeatAssignation seatAssignation = new SeatAssignationImpl(flightCentral);
        SeatMap seatMap = new SeatMapImpl(flightCentral);

        final Remote remoteNotification = UnicastRemoteObject.exportObject(flightNotification,0);
        final Remote remoteAdministration = UnicastRemoteObject.exportObject(flightAdministration,0);
        final Remote remoteAssignation = UnicastRemoteObject.exportObject(seatAssignation,0);
        final Remote remoteMap = UnicastRemoteObject.exportObject(seatMap,0);

        final Registry registry = LocateRegistry.getRegistry();
        registry.rebind(FlightNotification.class.getName(),remoteNotification);
        registry.rebind(FlightAdministration.class.getName(),remoteAdministration);
        registry.rebind( SeatAssignation.class.getName(),remoteAssignation);
        registry.rebind(SeatMap.class.getName(),remoteMap);

        logger.info("Services Exposed");



    }
}
