package flightService.client.arguments;



import java.util.Properties;

/**
 * La información de cuál es la acción a realizar se recibe a través de argumentos de línea de comando al llamar al script del cliente de asignación de asientos y el resultado se debe imprimir en pantalla.
 * Por ejemplo:
 * $> ./run-seatAssign -DserverAddress=xx.xx.xx.xx:yyyy
 * -Daction=actionName -Dflight=flightCode
 * [ -Dpassenger=name | -Drow=num | -Dcol=L | -DoriginalFlight=originFlightCode ]
 */
public class ArgumentsSeatAssignation{

    private String serverAddress;
    private String action;
    private String flightCode;
    private String passenger;
    private Integer row;
    private Character col;
    private String originalFlight;



    private static final String SERVER="serverAddress";
    private static final String ACTION="action";
    private static final String PASSENGER="passenger";
    private static final String FLIGHT="flight";
    private static final String ROW="row";
    private static final String COL="col";
    private static final String ORIGINAL_FLIGHT="originalFlight";


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

    public String getAction() {
        return action;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getPassenger() {
        return passenger;
    }

    public Integer getRow() {
        return row;
    }

    public Character getCol() {
        return col;
    }

    public String getOriginalFlight() {
        return originalFlight;
    }

    public void parseArguments() throws IllegalArgumentException {

        Properties properties = System.getProperties();

        if(properties.containsKey(SERVER)){

            this.serverAddress = properties.getProperty(SERVER);
        }else{
            throw new IllegalArgumentException("Invalid argument for server address");
        }

        if(properties.containsKey(ACTION)){

            this.action = properties.getProperty(ACTION);
        }else{
            throw new IllegalArgumentException("Invalid argument for action");
        }

        if(properties.containsKey(FLIGHT)){

            this.flightCode = properties.getProperty(FLIGHT);
        }else{
            throw new IllegalArgumentException("Invalid argument for flight");
        }



        if(action.equals("assign") || action.equals("move") || action.equals("alternatives")
                || action.equals("changeTicket") ){
            if(properties.containsKey(PASSENGER)){

                this.passenger = properties.getProperty(PASSENGER);
            }else{
                throw new IllegalArgumentException("Invalid argument for passenger");
            }
        }

        if(action.equals("assign") || action.equals("move") || action.equals("status")){
            if(properties.containsKey(ROW)){
                this.row = Integer.parseInt(properties.getProperty(ROW));
            }else{
                throw new IllegalArgumentException("Invalid argument for row");
            }

            if(properties.containsKey(COL)){
                this.col = properties.getProperty(COL).charAt(0);
            }else{
                throw new IllegalArgumentException("Invalid argument for col");
            }
        }

        if(action.equals("changeTicket")){
            if(properties.containsKey(ORIGINAL_FLIGHT)){

                this.originalFlight = properties.getProperty(ORIGINAL_FLIGHT);
            }else{
                throw new IllegalArgumentException("Invalid argument for original flight");
            }
        }
    }

}
