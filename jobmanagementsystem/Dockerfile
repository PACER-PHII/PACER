#Use the maven as "builder" and then use builder to share an artifact to the real tomcat service
FROM maven:3.5.4-jdk-8 as builder
MAINTAINER Mike Riley "michael.riley@gtri.gatech.edu"
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn clean install -DskipTests -f /usr/src/app/

FROM java:8-jdk
#move the WAR for contesa to the webapps directory
COPY --from=builder /usr/src/app/target/JobManagementSystem-0.0.1-SNAPSHOT.jar /usr/src/app/JobManagementSystem.jar
WORKDIR /usr/src/app
ADD wait-for-it.sh .
EXPOSE 8080
#CMD ["tail", "-f", "/dev/nulljava"]
CMD ["java", "-jar", "/usr/src/app/JobManagementSystem.jar"]

