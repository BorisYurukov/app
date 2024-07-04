FROM gradle:7.0.2-jdk8 AS build

COPY . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build --no-daemon

FROM openjdk:8-jdk-alpine

COPY --from=build home/gradle/src/build/libs/weare-0.0.1-SNAPSHOT.jar /demo.jar

CMD ["java", "-jar", "/demo.jar"]