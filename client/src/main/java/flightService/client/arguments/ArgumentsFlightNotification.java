package flightService.client.arguments;


import java.util.Properties;

public class ArgumentsFlightNotification {
    private String address;
    private String passenger;
    private String flightCode;


    private static final String SERVER="serverAddress";
    private static final String PASSENGER="passenger";
    private static final String FLIGHT="flight";




    public String getAddress(){
        return address;
    }

    public String getPassenger() {
        return passenger;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void parseArguments() throws IllegalArgumentException {

        Properties properties = System.getProperties();


        if(properties.containsKey(SERVER)){

            this.address = properties.getProperty(SERVER);
        }else{
            throw new IllegalArgumentException("Invalid argument for server address");
        }

        if(properties.containsKey(PASSENGER)){

            this.passenger = properties.getProperty(PASSENGER);
        }else{
            throw new IllegalArgumentException("Invalid argument for passenger");
        }

        if(properties.containsKey(FLIGHT)){

            this.flightCode = properties.getProperty(FLIGHT);
        }else{
            throw new IllegalArgumentException("Invalid argument for flight");
        }

    }

}
