package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.*;
import ar.edu.itba.models.utils.RowColumnPair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface FlightAdministration extends Remote {

    /**
     * Agregar un modelo de avión a partir de un nombre y la cantidad de filas y columnas de asientos
     * para cada categoría de asiento.
     */
    void addPlaneModel(String model, EnumMap<SeatCategory, RowColumnPair> categories) throws RemoteException;

    /**
     * Agregar un vuelo a partir del nombre del modelo de avión, un código de vuelo,
     * un código del aeropuerto destino y el detalle de los tickets de vuelo
     * indicando para cada ticket el nombre del pasajero y la categoría del ticket
     * (categoría de asiento comprada).
     */
    void addFlight(String modelName, String flightCode, String destinationAirportCode, ConcurrentHashMap<String, Ticket> tickets) throws RemoteException;

    /**
     * Consultar el estado de un vuelo a partir del código de vuelo, indicando si está pendiente,
     * cancelado o confirmado.
     * Si no existe un vuelo con ese código se debe arrojar un error
     */
    FlightStatus getFlightStatus(String flightCode) throws RemoteException;

    /**
     * Confirmar un vuelo pendiente a partir del código de vuelo.
     * Si no existe un vuelo con ese código o no tiene el estado pendiente arroja un error
     */
    void confirmFlight(String flightCode) throws RemoteException;

    /**
     * Cancelar un vuelo pendiente a partir del código de vuelo.
     * Si no existe un vuelo con ese código o no tiene el estado pendiente arroja un error
     */
    void cancelFlight(String flightCode) throws RemoteException;

    /**
     * Forzar el cambio de tickets de vuelos cancelados por tickets de vuelos alternativos.
     * Se considera vuelo alternativo a un vuelo pendiente que tenga el mismo aeropuerto destino que el del vuelo original
     */
    String
    forceTicketChangeForCancelledFlights() throws RemoteException;


}
