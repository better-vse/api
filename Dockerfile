FROM gradle:7.4.2-jdk17 AS build

RUN mkdir /app
WORKDIR /app

COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts
COPY ./src  /app/src

RUN gradle bootJar


FROM openjdk:17-slim AS server

RUN mkdir /app
WORKDIR /app

COPY --from=build /app/build/libs/api*.jar /app/application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]