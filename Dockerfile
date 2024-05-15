FROM mcr.microsoft.com/playwright/java:v1.49.0-noble
#COPY "/mnt/c/dev/reserv-crosshero/crosshero/target/crosshero-0.0.1-SNAPSHOT.jar" "/tmp/crosshero-0.0.1-SNAPSHOT.jar"
COPY "/target/crosshero-0.0.1-SNAPSHOT.jar" "/tmp/crosshero-0.0.1-SNAPSHOT.jar"
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "crosshero-0.0.1-SNAPSHOT.jar"]