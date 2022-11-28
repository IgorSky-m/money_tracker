FROM maven:3.8.4-jdk-11-slim AS build
WORKDIR /app
COPY src /src
COPY pom.xml /
RUN mvn -f /pom.xml clean package


FROM openjdk:11-jre-slim
COPY --from=build /target/calculation-bot-1.0.0-SNAPSHOT.jar /app/calculation-bot-1.0.0-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/calculation-bot-1.0.0-SNAPSHOT.jar"]
