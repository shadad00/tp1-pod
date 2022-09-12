package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.Flight;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatAssignation extends Remote {

    /**
     * Consultar si un asiento está libre u ocupado a partir del código de vuelo,
     * el número de la fila y la letra de la columna del asiento
     *
     * @param flightCode
     * @param row
     * @param col
     * @return
     * @throws RemoteException
     */
    String isSeatFree(String flightCode, Integer row, Character col) throws RemoteException;

    /**
     * Asignar un asiento libre a un pasajero a partir del código de vuelo y el número de la fila
     * y la letra de la columna del asiento
     * @param flightCode
     * @param passenger
     * @param row
     * @param col
     * @throws RemoteException
     */
    void assignSeat(String flightCode, String passenger, Integer row, Character col) throws RemoteException;

    /**
     * Mover a un pasajero de un asiento asignado a un asiento libre del mismo vuelo,
     * a partir del código de vuelo, el nombre del pasajero y el número de la fila y
     * la letra de la columna del asiento libre.
     * @param passenger
     * @throws RemoteException
     */
    void movePassenger(String flightCode, String passenger, Integer row, Character col) throws RemoteException;

    /**
     * Listar la disponibilidad en los vuelos alternativos de un pasajero, a partir del código de vuelo original
     * y el nombre del pasajero
     * @param flightCode
     * @param passenger
     * @throws RemoteException
     */
    List<Flight> checkAlternativeFlights(String flightCode, String passenger) throws RemoteException;

    /**
     * Cambiar el ticket de vuelo, a partir del nombre del pasajero, el código de vuelo original
     * y el código de vuelo alternativo
     * @throws RemoteException
     */
    void changeTicket(String passenger, String oldFlight, String newFlight) throws RemoteException;
}
