# Trabajo Práctico Especial 1: Tickets de Vuelo

## Ubicacion de los Materiales 🧭
Archivos fuente del servidor se encuentran dentro de las subcarpetas
```
./server/src/main/java
```
Archivos fuente de los clientes se encuentran dentro de las subcarpetas
```
./client/src/main/java
```
Archivos ejecutables server
```
./server/target/tp1-g14-server-1.0-SNAPSHOT/
```
Archivos ejecutables clientes
```
./client/target/tp1-g14-client-1.0-SNAPSHOT/
```

## Instrucciones de Compilacion 🛠️
Generacion de los .tar.gz a la carpeta target/
```
mvn clean install
```
Extraccion de los .tar.gz con los ejecutables
```
tar -xvf ./server/target/tp1-g14-server-1.0-SNAPSHOT.tar.gz
tar -xvf ./client/target/tp1-g14-client-1.0-SNAPSHOT.tar.gz
```



## Ejecución 🚀
Para correr el registry
```
./server/target/tp1-g14-server-1.0-SNAPSHOT/run-registry.sh
```
Para correr el server
```
./server/target/tp1-g14-server-1.0-SNAPSHOT/run-server.sh
```

Para ejecutar el cliente de administracion de vuelos
```
./client/target/tp1-g14-client-1.0-SNAPSHOT/run-admin -DserverAddress=xx.xx.xx.xx:yyyy 
-Daction=actionName [ -DinPath=filename | -Dflight=flightCode ]
```
Para ejecutar el cliente de asignacion de asientos
```
./client/target/tp1-g14-client-1.0-SNAPSHOT/run-seatAssign -DserverAddress=xx.xx.xx.xx:yyyy 
-Daction=actionName -Dflight=flightCode [ -Dpassenger=name | -Drow=num | -Dcol=L | 
-DoriginalFlight=originFlightCode ]
```

Para ejecutar el cliente de Mapa de asientos
```
./client/target/tp1-g14-client-1.0-SNAPSHOT/run-seatMap -DserverAddress=xx.xx.xx.xx:yyyy 
-Dflight=flightCode [ -Dcategory=catName | -Drow=rowNumber ] -DoutPath=output.csv
```

Para ejecutar el cliente de notificacion de usuarios
```
./client/target/tp1-g14-client-1.0-SNAPSHOT/run-notifications -DserverAddress=xx.xx.xx.xx:yyyy 
-Dflight=flightCode -Dpassenger=name
```


## Autores 💭
* **Gaspar Budó Berra**
* **Bruno Squillari**
* **Facundo Zimbimbakis**
* **Santiago Hadad**
* **Marcos Dedeu**