FROM maven:3.6.0-jdk-8 AS builder
WORKDIR /usr/src/cql_storage
ADD . .
RUN mvn clean install -DskipTests -f /usr/src/cql_storage

FROM java:8-jdk
#move the WAR for contesa to the webapps directory
COPY --from=builder /usr/src/cql_storage/target/CQLStorage-0.0.1-SNAPSHOT.jar /usr/src/app/CQLStorage.jar
WORKDIR /usr/src/app
ADD wait-for-it.sh .
EXPOSE 8080
#CMD ["tail", "-f", "/dev/nulljava"]
CMD ["java", "-jar", "/usr/src/app/CQLStorage.jar"]
