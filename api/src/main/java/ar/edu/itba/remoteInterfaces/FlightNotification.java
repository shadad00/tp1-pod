package ar.edu.itba.remoteInterfaces;

import ar.edu.itba.models.Passenger;

import java.rmi.Remote;

public interface FlightNotification extends Remote {
    /**
     * Registrar a un pasajero para que éste sea notificado de los eventos relacionados a un ticket de vuelo pendiente o cancelado a partir del código identificador del vuelo y el nombre del pasajero
     * @param flightCode
     * @param passenger
     */
    void registerUser(String flightCode, Passenger passenger);
}
