#
# Build stage
#
FROM maven:3.8.6-eclipse-temurin-17-alpine AS build
COPY src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package

#
# Package stage
#
FROM eclipse-temurin:17
COPY --from=build /app/target/decide-discord-1.0-SNAPSHOT-jar-with-dependencies.jar /app/decide-discord.jar
ENTRYPOINT ["java","-jar","/app/decide-discord.jar"]