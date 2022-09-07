package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;

public interface FlightAdministration extends Remote {

    /**
     * Agregar un modelo de avión a partir de un nombre y la cantidad de filas y columnas de asientos para cada categoría de asiento
     * @param plane
     * @throws RemoteException
     */
    void addPlaneModel(String modelName, EnumMap<SeatCategory, Pair<Integer>>) throws RemoteException;

    /**
     * Agregar un vuelo a partir del nombre del modelo de avión, un código de vuelo, uncódigo del aeropuerto destino 
     * y el detalle de los tickets de vuelo indicando para cada ticket el nombre del pasajero y la categoría del ticket (categoría de asiento comprada)
     * @param modelName
     * @param flightCode
     * @param destinationAirportCode
     * @param seatList
     * @throws RemoteException
     */
    void addFlight(String modelName, String flightCode, String destinationAirportCode, List<Seat> seatList ) throws RemoteException;

    /**
     * Consultar el estado de un vuelo a partir del código de vuelo, indicando si estápendiente, cancelado o confirmado.
     * Si no existe un vuelo con ese código se debearrojar un error
     * @param flightCode
     * @return
     * @throws RemoteException
     */
    FlightStatus getFlightStatus(String flightCode) throws RemoteException;

    /**
     * Confirmar un vuelo pendiente a partir del código de vuelo.
     * Si no existe un vuelo con ese código o no tiene el estado pendiente arroja un error
     * @param flightCode
     * @throws RemoteException
     */
    void confirmFlight(String flightCode) throws RemoteException;

    /**
     * Cancelar un vuelo pendiente a partir del código de vuelo.
     * Si no existe un vuelocon ese código o no tiene el estado pendiente arroja un error
     * @param flightCode
     * @throws RemoteException
     */
    void cancelFlight(String flightCode) throws RemoteException;

    /**
     * Forzar el cambio de tickets de vuelos cancelados por tickets de vuelosalternativos.
     * Se considera vuelo alternativo a un vuelo pendiente que tenga elmismo aeropuerto destino que el del vuelo original
     * @throws RemoteException
     */
    void forceTicketChangeForCancelledFlights() throws  RemoteException;


}
