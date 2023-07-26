FROM openjdk:17-jdk-alpine
EXPOSE 8080
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} gateway-service-1.0.jar
ENTRYPOINT ["java","-jar","gateway-service-1.0.jar", ""]