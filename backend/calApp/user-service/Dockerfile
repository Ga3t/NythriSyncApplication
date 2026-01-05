FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
