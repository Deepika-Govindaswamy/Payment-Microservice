FROM openjdk:17-jdk

WORKDIR /app

# copying the jarfile into docker image
COPY target/payment-processor-0.0.1-SNAPSHOT.jar /app/payment-processor-service.jar

EXPOSE 8081

CMD ["java", "-jar", "payment-processor-service.jar"]