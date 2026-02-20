FROM gradle:9.2.1-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y openssl && rm -rf /var/lib/apt/lists/*
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar
COPY init-keys.sh init-keys.sh
RUN chmod +x init-keys.sh
ENTRYPOINT ["./init-keys.sh"]
