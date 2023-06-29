FROM openjdk:20-jdk
WORKDIR /app
COPY ./build/libs/users-1.0.jar ./runner.jar
ENTRYPOINT ["java", "-jar", "/app/runner.jar"]