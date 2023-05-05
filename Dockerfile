FROM openjdk:11-jre-slim
ADD target/*.jar airports.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","airports.jar"]
