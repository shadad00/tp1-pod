package flightService.client.arguments;


import java.util.Properties;

public class ArgumentsSeatMap {
    private String serverAddress;
    private String flightCode;
    private String category;
    private String row;
    private String outPath;
    private String address;
    private int port;


    private static final String SERVER="serverAddress";
    private static final String FLIGHT="flight";
    private static final String ROW="row";
    private static final String OUT_PATH="outPath";
    private static final String CATEGORY="category";


    public String getServerAddress() {
        return serverAddress;
    }

    public Integer getPort(){
        return port;
    }

    public String getAddress(){
        return address;
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

            String[] strings = serverAddress.split(":");
            address = strings[0];
            port = Integer.parseInt(strings[1]);
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
        }
//        else{
//            throw new IllegalArgumentException("Invalid argument for row");
//        }

        if(properties.containsKey(OUT_PATH)){

            this.outPath = properties.getProperty(OUT_PATH);
        }
//        else{
//            throw new IllegalArgumentException("Invalid argument for output path");
//        }
        //TODO: add outPath

        if(properties.containsKey(CATEGORY)){

            this.category = properties.getProperty(CATEGORY);
        }
//        else{
//            throw new IllegalArgumentException("Invalid argument for category");
//        }


    }
}
