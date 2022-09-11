package flightService.client;

import ar.edu.itba.exceptions.IllegalUserRegistration;
import ar.edu.itba.remoteInterfaces.FlightNotification;
import ar.edu.itba.remoteInterfaces.Notifier;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.client.arguments.ArgumentsFlightNotification;
import flightService.client.arguments.ArgumentsSeatAssignation;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientFlightNotification {
    public static void main(String[] args) {
        try {
            ArgumentsFlightNotification clientArguments = new ArgumentsFlightNotification();

            // Parsing the arguments
            try {
                clientArguments.parseArguments();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

            final Notifier notifier = new NotifierImpl();
            final Remote remoteNotifier = UnicastRemoteObject.exportObject(notifier,0);



            final Registry registry = LocateRegistry.getRegistry(clientArguments.getAddress(), clientArguments.getPort());

            final FlightNotification service = (FlightNotification) registry.lookup(FlightNotification.class.getName());

            service.registerUser(clientArguments.getFlightCode(), clientArguments.getPassenger(), notifier);//TODO Notifier


        } catch (RemoteException re) {
            System.out.println("ERROR: Exception in the remote server");
        } catch (NotBoundException nbe) {
            System.out.println("ERROR: Service not bound");
        }
//        catch (MalformedURLException me) {
//            System.out.println("ERROR: Malformed URL");
//        }
        catch (IllegalUserRegistration e) {
            throw new RuntimeException(e);
        }
    }
}
