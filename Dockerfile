FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY build/libs/BackEndComprasPruebaTecnica-0.0.1.jar app.jar
COPY src/main/resources/keys /app/keys
EXPOSE 8025
ENTRYPOINT ["java", "-jar", "app.jar"]