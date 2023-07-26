FROM openjdk:17-jdk-alpine
EXPOSE 8761
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} eureka-server-1.0.jar
ENTRYPOINT ["java","-jar","eureka-server-1.0.jar", ""]