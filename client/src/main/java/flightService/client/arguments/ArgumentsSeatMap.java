package flightService.client.arguments;


import java.util.Properties;

public class ArgumentsSeatMap {
    private String flightCode;
    private String category;
    private Integer row;
    private String outPath;
    private String address;


    private static final String SERVER="serverAddress";
    private static final String FLIGHT="flight";
    private static final String ROW="row";
    private static final String OUT_PATH="outPath";
    private static final String CATEGORY="category";



    public String getAddress(){
        return address;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getCategory() {
        return category;
    }

    public Integer getRow() {
        return row;
    }

    public String getOutPath() {
        return outPath;
    }

    public void parseArguments() throws IllegalArgumentException {

        Properties properties = System.getProperties();


        if(properties.containsKey(SERVER)){
            this.address = properties.getProperty(SERVER);

        }else{
            throw new IllegalArgumentException("Invalid argument for server address");
        }

        if(properties.containsKey(FLIGHT)){

            this.flightCode = properties.getProperty(FLIGHT);
        }else{
            throw new IllegalArgumentException("Invalid argument for flight");
        }

        if(properties.containsKey(ROW)){

            this.row = Integer.parseInt(properties.getProperty(ROW));

        }

        if(properties.containsKey(OUT_PATH)){

            this.outPath = properties.getProperty(OUT_PATH);
        }
        else{
            throw new IllegalArgumentException("Invalid argument for output path");
        }

        if(properties.containsKey(CATEGORY)){
            if (row!=null){
                throw new IllegalArgumentException("Cannot filter by category and row");
            }
            this.category = properties.getProperty(CATEGORY);
        }


    }
}
