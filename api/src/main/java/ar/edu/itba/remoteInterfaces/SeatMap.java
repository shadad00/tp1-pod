package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.SeatCategory;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatMap extends Remote{

    /**
     * Consultar el mapa de asientos de un vuelo, a partir del código del mismo
     * @param flightCode
     * @throws RemoteException
     */
    String getSeatMap(String flightCode) throws RemoteException;

    /**
     * Consultar el mapa de asientos de un vuelo de una categoria indicada, a partir del código del mismo
     * @param flightCode
     * @throws RemoteException
     */
    String getSeatMapByCategory(String flightCode , SeatCategory category) throws RemoteException;

    /**
     * Consultar el mapa de asientos de un vuelo de una fila indicada, a partir del código del mismo
     * @param flightCode
     * @throws RemoteException
     */
    String getSeatMapByRow(String flightCode , int row) throws RemoteException;

}
