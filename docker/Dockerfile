# Alpine Linux with OpenJDK JRE
FROM openjdk:11-slim as runtime

# Copy war file
COPY ted-0.0.1-SNAPSHOT.jar /ted.jar

# run the app
ENTRYPOINT [ "sh", "-c", "java -jar ted.jar" ]