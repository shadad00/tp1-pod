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

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) throws RemoteException , AlreadyBoundException {
        logger.info("flightService Server Starting ...");
        FlightCentral flightCentral = new FlightCentral();
        FlightNotificationImpl flightNotification = new FlightNotificationImpl(flightCentral);
        flightCentral.setNotificator(flightNotification);
        FlightAdministration flightAdministration = new FlightAdministrationImpl(flightCentral);
        SeatAssignation seatAssignation = new SeatAssignationImpl(flightCentral);
        SeatMap seatMap = new SeatMapImpl(flightCentral);

        final Remote remoteNotification = UnicastRemoteObject.exportObject(flightNotification,0);
        final Remote remoteAdministration = UnicastRemoteObject.exportObject(flightAdministration,9999);
        final Remote remoteAssignation = UnicastRemoteObject.exportObject(seatAssignation,0);
        final Remote remoteMap = UnicastRemoteObject.exportObject(seatMap,0);

        final Registry registry = LocateRegistry.getRegistry();
        registry.bind(FlightNotification.class.getName(),remoteNotification);
        registry.bind(FlightAdministration.class.getName(),remoteAdministration);
        registry.bind( SeatAssignation.class.getName(),remoteAssignation);
        registry.bind(SeatMap.class.getName(),remoteMap);

        logger.info("Services Exposed");



    }
}
