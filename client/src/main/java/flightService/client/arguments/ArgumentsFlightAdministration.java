package flightService.client.arguments;

import java.util.Properties;

/**
 *
 * La información de cuál es la acción a realizar se recibe a través de argumentos de línea de
 * comando al llamar al script del cliente de administración de vuelos y el resultado se debe imprimir
 * en pantalla.
 *
 *  Por ejemplo:
 * $> ./run-admin -DserverAddress=xx.xx.xx.xx:yyyy -Daction=actionName
 * [ -DinPath=filename | -Dflight=flightCode ]
 *
 *
 * ● run-admin es el script que invoca a la clase del cliente de administración de vuelos
 * 
 * ● xx.xx.xx.xx:yyyy es la dirección IP y el puerto donde está publicado el servicio de administración de vuelos
 * ● actionName es el nombre de la acción a realizar
 *      ○ models: Agrega una lote de modelos de aviones (ver detalle más abajo)
 *      ○ flights: Agrega un lote de vuelos (ver detalle más abajo)
 *      ○ status: Consulta el estado del vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente
 *      ○ confirm: Confirma el vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente
 *      ○ cancel: Cancela el vuelo de código flightCode. Deberá imprimir en pantalla el estado del vuelo luego de invocar a la acción o el error correspondiente
 *      ○ reticketing: Fuerza el cambio de tickets de vuelos cancelados por tickets de vuelos alternativos
 *
 * ● El path del archivo necesario para cargar un lote de modelos de aviones, se recibe con -DinPath=filename
 */

public class ArgumentsFlightAdministration {

    private String action;
    private String inPath;
    private String flightCode;
    private String address;


    private static final String SERVER="serverAddress";
    private static final String ACTION="action";
    private static final String IN_PATH="inPath";
    private static final String FLIGHT="flight";


    public String getAddress(){
        return  address;
    }

    public String getAction() {
        return action;
    }

    public String getInPath() {
        return inPath;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public void parseArguments() throws IllegalArgumentException{

        Properties properties = System.getProperties();




        if(properties.containsKey(SERVER)){
             address = properties.getProperty(SERVER);
;
        }
        else{
            throw new IllegalArgumentException("Invalid argument for server address");
        }

        if(properties.containsKey(ACTION)){

            this.action = properties.getProperty(ACTION);
        }else{
            throw new IllegalArgumentException("Invalid argument for action");
        }

        if(action.equals("flights") || action.equals("models")){
            if(properties.containsKey(IN_PATH)){

                this.inPath = properties.getProperty(IN_PATH);
            }else{
                throw new IllegalArgumentException("Invalid argument for input path file");
            }
        }else if(action.equals("status") || action.equals("confirm") || action.equals("cancel")){
            if(properties.containsKey(FLIGHT)){

                this.flightCode = properties.getProperty(FLIGHT);
            }else{
                throw new IllegalArgumentException("Invalid argument for flight");
            }
        }
    }

}
