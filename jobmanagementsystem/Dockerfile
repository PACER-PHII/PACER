#Use the maven as "builder" and then use builder to share an artifact to the real tomcat service
FROM maven:3.8.4-openjdk-17 as builder
MAINTAINER Mike Riley "michael.riley@gtri.gatech.edu"
COPY . /usr/src/app
#Expectation is that the user provides a PKCS12 key to the system
#NOTE: Requires  environment definitions that name the key file, password, and alias
#See attached docker-compsoe-apps.yml file
COPY *.p12 /usr/src/app/src/main/resources/keystore/
WORKDIR /usr/src/app
RUN mvn clean install -DskipTests -f /usr/src/app/

FROM openjdk:17
#move the WAR for contesa to the webapps directory
COPY --from=builder /usr/src/app/target/JobManagementSystem-0.0.1-SNAPSHOT.jar /usr/src/app/JobManagementSystem.jar
WORKDIR /usr/src/app
EXPOSE 443
#CMD ["tail", "-f", "/dev/nulljava"]
CMD ["java", "-jar", "/usr/src/app/JobManagementSystem.jar"]

