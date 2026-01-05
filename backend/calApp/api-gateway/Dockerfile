FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY target/api-gateway-0.0.1-SNAPSHOT.jar app.jar
RUN apk add --no-cache ca-certificates

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]