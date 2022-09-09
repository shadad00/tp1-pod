package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Seat;
import ar.edu.itba.models.Ticket;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SeatAssignation extends Remote {

    /**
     * Consultar si un asiento está libre u ocupado a partir del código de vuelo, el número de la fila y la letra de la columna del asiento
     * @param flightCode
     * @param row
     * @param col
     * @return
     * @throws RemoteException
     */
    boolean isSeatFree(String flightCode, Integer row, Integer col) throws RemoteException;

    /**
     * Asignar un asiento libre a un pasajero a partir del código de vuelo y el númerode la fila y la letra de la columna del asiento
     * @param flightCode
     * @param passenger
     * @param row
     * @param col
     * @throws RemoteException
     */
    void assignSeat(String flightCode, String passenger, Integer row, Integer col) throws RemoteException;

    /**
     * Mover a un pasajero de un asiento asignado a un asiento libre del mismovuelo, a partir del código de vuelo, el nombre del pasajero y el número de la fila y laletra de la columna del asiento libre
     * @param passenger
     * @throws RemoteException
     */
    void movePassenger(String flightCode, String passenger, Integer row, Integer col) throws RemoteException;

    /**
     * Listar la disponibilidad en los vuelos alternativos de un pasajero, a partir del código de vuelo original y el nombre del pasajero
     * @param flight
     * @throws RemoteException
     */
    List<Flight> checkAlternativeFlights(Flight flight, Ticket ticket) throws RemoteException;

    /**
     * Cambiar el ticket de vuelo, a partir del nombre del pasajero, el código de vuelooriginal y el código de vuelo alternativo
     * @throws RemoteException
     */
    void changeTicket(Passenger passenger, Flight original, Flight alternative) throws RemoteException;
}
