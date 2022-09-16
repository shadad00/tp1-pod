mvn clean install
tar -xvf ./server/target/tp1-g14-server-1.0-SNAPSHOT-bin.tar.gz
tar -xvf ./client/target/tp1-g14-client-1.0-SNAPSHOT-bin.tar.gz
cd tp1-g14-server-1.0-SNAPSHOT/
chmod u+x run-server.sh run-registry.sh
./run-registry.sh&
./run-server.sh&
cd ..
cd tp1-g14-client-1.0-SNAPSHOT/
chmod u+x *.sh

