FROM openjdk:17-jdk-alpine
EXPOSE 8084
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} account-service-1.0.jar
ENTRYPOINT ["java","-jar","account-service-1.0.jar", ""]