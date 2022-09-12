#mvn clean install 
cd server/target
tar -xvf tp1-g14-server-1.0-SNAPSHOT-bin.tar.gz
cd tp1-g14-server-1.0-SNAPSHOT/
chmod u+x run-server.sh run-registry.sh 
./run-registry.sh& 
./run-server.sh&
cd ../../../client/target/
tar -xvf tp1-g14-client-1.0-SNAPSHOT-bin.tar.gz
chmod u+x *
cd tp1-g14-client-1.0-SNAPSHOT/

