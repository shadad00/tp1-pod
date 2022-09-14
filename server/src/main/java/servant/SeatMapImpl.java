package servant;

import ar.edu.itba.models.*;
import ar.edu.itba.remoteInterfaces.SeatMap;
import flightService.server.FlightCentral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.Optional;

/**
 * Ejemplo
 * | 01 A * | 01 B * | 01 C * | BUSINESS
 * | 02 A * | 02 B J | 02 C * | BUSINESS
 * | 03 A * | 03 B * | 03 C * | PREMIUM_ECONOMY
 * | 04 A * | 04 B A | 04 C * | PREMIUM_ECONOMY
 * | 05 A * | 05 B * | 05 C * | PREMIUM_ECONOMY
 * | 06 A * | 06 B * | 06 C * | 06 D * | 06 E * | ECONOMY
 * | 07 A * | 07 B * | 07 C * | 07 D * | 07 E * | ECONOMY
 */
public class SeatMapImpl implements SeatMap {
    private static final Logger LOG = LoggerFactory.getLogger(SeatMapImpl.class);
    private final FlightCentral flightCentral;

    public SeatMapImpl(FlightCentral flightCentral) {
        this.flightCentral = flightCentral;
    }

    @Override
    public String getSeatMap(String flightCode) throws RemoteException {
        StringBuilder answer = new StringBuilder();
        for(SeatCategory category : SeatCategory.values()){
            answer.append(getSeatMapByCategory(flightCode,category));
        }
        return answer.toString();
    }

    @Override
    public String getSeatMapByCategory(String flightCode, SeatCategory category) throws RemoteException {
        Flight flight = this.flightCentral.getFlight(flightCode);
        if(flight == null){
            LOG.info("Flight does not exist");
            throw new IllegalArgumentException("Flight does not exist");
        }

        StringBuilder answer = new StringBuilder();
        FlightTicketsMap categorySeats = flight.getFlightTicketsMapByCategory(category);
        if (categorySeats != null) {
            for(int i = 0; i < categorySeats.getRowsNumber(); i++){
                answer.append(getSeatMapByRowWithCategory(flight,categorySeats.getInitialRow()+i,category));
            }
        }
        return answer.toString();
    }



    @Override
    public String getSeatMapByRow(String flightCode, int row) throws RemoteException {
        Flight flight = this.flightCentral.getFlight(flightCode);
        if(flight == null){
            LOG.info("Flight does not exist");
            throw new IllegalArgumentException("Flight does not exist");
        }

        SeatCategory category;
        if((category = flight.getCategoryFromRow(row)) == null) {
            LOG.info("Row does not belong to any category");
            throw new IllegalArgumentException("Row does not belong to any category");
        }
        return getSeatMapByRowWithCategory(flight,row,category);
    }

    private String rowToString(SeatCategory category, Ticket[] row, Integer rowNumber){
        if(category == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        String rowString = String.format("%02d",rowNumber);
        char col = 0;
        for (Ticket ticket : row) {
            stringBuilder.append("| ").append(rowString).append(' ').append((char)('A' + col)).append(' ').append(ticket!=null? ticket.getPassenger().charAt(0) : '*').append(" |");
            col++;
        }
        stringBuilder.append("\t").append(category.name()).append('\n');
        return stringBuilder.toString();
    }


    private String getSeatMapByRowWithCategory(Flight flight , int row , SeatCategory category){
        Ticket[] ticketRow = flight.getFlightTicketsMapByCategory(category).getRow(row);
        return rowToString(category,ticketRow,row);
    }



}
