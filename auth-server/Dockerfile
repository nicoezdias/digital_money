FROM openjdk:17-jdk-alpine
EXPOSE 8090
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} auth-service-1.0.jar
ENTRYPOINT ["java","-jar","auth-service-1.0.jar", ""]