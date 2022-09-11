package flightService.client.arguments;


import java.util.Properties;

public class ArgumentsSeatMap {
    private String serverAddress;
    private String flightCode;
    private String category;
    private String row;
    private String outPath;



    private static final String SERVER="serverAddress";
    private static final String FLIGHT="flight";
    private static final String ROW="row";
    private static final String OUT_PATH="outPath";
    private static final String CATEGORY="category";


    public String getServerAddress() {
        return serverAddress;
    }

    public Integer getPort(){
        String[] strings = serverAddress.split(":");
        return Integer.getInteger(strings[1]);
    }

    public String getAddress(){
        String[] strings = serverAddress.split(":");
        return strings[0];
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getCategory() {
        return category;
    }

    public String getRow() {
        return row;
    }

    public String getOutPath() {
        return outPath;
    }

    public void parseArguments() throws IllegalArgumentException {

        Properties properties = System.getProperties();


        if(properties.containsKey(SERVER)){

            this.serverAddress = properties.getProperty(SERVER);
        }else{
            throw new IllegalArgumentException("Invalid argument for server address");
        }

        if(properties.containsKey(FLIGHT)){

            this.flightCode = properties.getProperty(FLIGHT);
        }else{
            throw new IllegalArgumentException("Invalid argument for flight");
        }

        if(properties.containsKey(ROW)){

            this.row = properties.getProperty(ROW);
        }else{
            throw new IllegalArgumentException("Invalid argument for row");
        }

        if(properties.containsKey(OUT_PATH)){

            this.serverAddress = properties.getProperty(OUT_PATH);
        }else{
            throw new IllegalArgumentException("Invalid argument for output path");
        }

        if(properties.containsKey(CATEGORY)){

            this.serverAddress = properties.getProperty(CATEGORY);
        }else{
            throw new IllegalArgumentException("Invalid argument for category");
        }


    }
}
