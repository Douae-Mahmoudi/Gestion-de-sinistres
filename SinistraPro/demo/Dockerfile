# Build avec Maven + Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Run avec Java 17 Alpine (léger)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8100
ENTRYPOINT ["java", "-jar", "app.jar"]