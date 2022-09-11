import ar.edu.itba.models.Flight;
import ar.edu.itba.models.Passenger;
import ar.edu.itba.models.Plane;
import ar.edu.itba.models.SeatCategory;
import ar.edu.itba.remoteInterfaces.SeatAssignation;
import flightService.servant.SeatAssignationImpl;
import flightService.server.FlightCentral;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.rmi.RemoteException;
import java.util.ArrayList;

public class SeatAssignationTest {

    FlightCentral flightCentral = new FlightCentral();

    private final int ROW = 1;
    private final char COLUMN = 'A';
    private final String FLIGHT_CODE = "AR1234";
    private final String MODEL_NAME = "Boeing 747";
    private final String DEST = "Buenos Aires";
    private final Plane plane = new Plane(MODEL_NAME);
    private final Passenger passenger = new Passenger("Juan", SeatCategory.BUSINESS);
    private final ArrayList<Passenger> passengers = new ArrayList<>();
    SeatAssignation seatAssignation = new SeatAssignationImpl(flightCentral);

    @BeforeEach
    public void addFlight() throws RemoteException {
        passengers.add(passenger);
        flightCentral.addFlight(FLIGHT_CODE, new Flight(FLIGHT_CODE, DEST, plane, passengers));
    }

    @Test
    public void isSeatFreeTest() throws RemoteException {
        assertTrue(seatAssignation.isSeatFree(ROW, COLUMN, FLIGHT_CODE));
    }

    @Test
    public void isSeatFreeTest2() throws RemoteException {
        flightCentral.getFlight(FLIGHT_CODE).addPassengerSeat(passenger, ROW, COLUMN);
        assertFalse(seatAssignation.isSeatFree(ROW, COLUMN, FLIGHT_CODE));
    }

    @Test
    public void assignSeatTest() throws RemoteException {
        seatAssignation.assignSeat(FLIGHT_CODE, ROW, COLUMN, passenger.getName());
        assertEquals(passenger.getName(), flightCentral.getFlight(FLIGHT_CODE).getPassenger(passenger.getName()).getName());
    }

    @Test
    public void movePassengerTest() throws RemoteException {
        flightCentral.getFlight(FLIGHT_CODE).addPassengerSeat(passenger, ROW, COLUMN);
        seatAssignation.movePassenger(FLIGHT_CODE, passenger.getName(), ROW, COLUMN);
        assertEquals(passenger.getName(), flightCentral.getFlight(FLIGHT_CODE).getPassenger(passenger.getName()).getName());
    }

    @Test
    public void movePassengerTest2() throws RemoteException {
        flightCentral.getFlight(FLIGHT_CODE).addPassengerSeat(passenger, ROW, COLUMN);
        assertThrows(RemoteException.class, () -> seatAssignation.movePassenger(FLIGHT_CODE, passenger.getName(), ROW, COLUMN));
    }

    @Test void checkAlternativeFlightsTest() throws RemoteException {
        Flight flight = flightCentral.getFlight(FLIGHT_CODE);
        flight.addPassengerSeat(passenger, ROW, COLUMN);
        assertThrows(RemoteException.class, () -> seatAssignation.checkAlternativeFlights(flight,passenger));
    }

}
