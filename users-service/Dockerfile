FROM openjdk:17-jdk-alpine
EXPOSE 8082
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} user-service-1.0.jar
ENTRYPOINT ["java","-jar","user-service-1.0.jar", ""]