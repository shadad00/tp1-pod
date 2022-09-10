package flightService.client.arguments;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.Properties;

public class ArgumentsFlightNotification {
    private String serverAddress;
    private String passenger;
    private String flightCode;


    private static final String SERVER="serverAddress";
    private static final String PASSENGER="passenger";
    private static final String FLIGHT="flight";


    public String getServerAddress() {
        return serverAddress;
    }

    public String getPassenger() {
        return passenger;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void parseArguments() throws InvalidArgumentException {

        Properties properties = System.getProperties();


        if(properties.containsKey(SERVER)){

            this.serverAddress = properties.getProperty(SERVER);
        }else{
            throw new InvalidArgumentException(new String[]{"Invalid argument for server address"});
        }

        if(properties.containsKey(PASSENGER)){

            this.passenger = properties.getProperty(PASSENGER);
        }else{
            throw new InvalidArgumentException(new String[]{"Invalid argument for passenger"});
        }

        if(properties.containsKey(FLIGHT)){

            this.flightCode = properties.getProperty(FLIGHT);
        }else{
            throw new InvalidArgumentException(new String[]{"Invalid argument for flight"});
        }

    }

}