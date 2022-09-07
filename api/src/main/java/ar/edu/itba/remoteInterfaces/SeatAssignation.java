package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SeatAssignation extends Remote {

    /**
     * Consultar si un asiento está libre u ocupado a partir del código de vuelo, el número de la fila y la letra de la columna del asiento
     * @param row
     * @param column
     * @param flight
     * @return
     * @throws RemoteException
     */
    boolean isSeatFree(int row, char column, String flight) throws RemoteException;

    /**
     * Asignar un asiento libre a un pasajero a partir del código de vuelo y el númerode la fila y la letra de la columna del asiento
     * @param passenger
     * @throws RemoteException
     */
    void assignSeat(String flightCode, int row, char col, String passenger) throws RemoteException;

    /**
     * Mover a un pasajero de un asiento asignado a un asiento libre del mismovuelo, a partir del código de vuelo, el nombre del pasajero y el número de la fila y laletra de la columna del asiento libre
     * @param passenger
     * @throws RemoteException
     */
    void movePassenger(String code, String passenger, int row, char column) throws RemoteException;

    /**
     * Listar la disponibilidad en los vuelos alternativos de un pasajero, a partir delcódigo de vuelo original y el nombre del pasajero
     * @param flight
     * @throws RemoteException
     */
    void checkAlternativeFlights(Flight flight, Passenger passenger) throws RemoteException;

    /**
     * Cambiar el ticket de vuelo, a partir del nombre del pasajero, el código de vuelooriginal y el código de vuelo alternativo
     * @throws RemoteException
     */
    void changeTicket(Passenger passenger, Flight original, Flight alternative) throws RemoteException;
}
