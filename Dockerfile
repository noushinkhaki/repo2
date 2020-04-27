FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} retailStoreApp.jar
ENTRYPOINT ["java","-jar","/retailStoreApp.jar"]