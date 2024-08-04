FROM maven:3.9.6-eclipse-temurin-21-jammy AS build
WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN mvn clean install


FROM eclipse-temurin:21-jdk-jammy
COPY --from=build /build/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]