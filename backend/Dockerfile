# Imagen del backend para Render (issue #60).
# Etapa 1: compila el .jar con Maven. Etapa 2: lo corre con solo el JRE.
FROM maven:3.9-eclipse-temurin-17 AS compilacion
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=compilacion /app/target/*.jar app.jar
# Render indica el puerto en la variable PORT; el backend lee SERVER_PORT.
ENV SERVER_PORT=8080
EXPOSE 8080
CMD ["sh", "-c", "java -Dserver.port=${PORT:-${SERVER_PORT}} -jar app.jar"]
