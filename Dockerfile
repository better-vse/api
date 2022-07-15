FROM gradle:7.4.2-jdk17 AS build

RUN mkdir /app
WORKDIR /app

COPY ./build.gradle.kts /app/build.gradle.kts
COPY ./settings.gradle.kts /app/settings.gradle.kts

# Download dependencies before copying app so it can be cached in a docker layer
RUN gradle clean build --info --no-daemon > /dev/null 2>&1 || true

COPY ./src  /app/src

RUN gradle bootJar --info --no-daemon


FROM openjdk:17-slim AS server

RUN mkdir /app
WORKDIR /app

COPY --from=build /app/build/libs/api*.jar /app/application.jar

ENTRYPOINT ["java", "-jar", "/app/application.jar"]