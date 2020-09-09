FROM openjdk:11
WORKDIR /
ADD build/libs/caloriecounter-0.0.1-SNAPSHOT.jar //
EXPOSE 8080
ENTRYPOINT [ "java", "-Dspring.profiles.active=docker", "-jar", "/caloriecounter-0.0.1-SNAPSHOT.jar"]
