cd auth-server/
mvn clean && mvn package -DskipTests
docker build . -t auth-server
cd ..

cd eureka-server/
mvn clean && mvn package -DskipTests
docker build . -t eureka-server
cd ..

cd gateway-api/
mvn clean && mvn package -DskipTests
docker build . -t gateway-api
cd ..

cd account-service/
mvn clean && mvn package -DskipTests
docker build . -t account-service
cd ..

cd users-service/
mvn clean && mvn package -DskipTests
docker build . -t users-service
cd ..

docker-compose up