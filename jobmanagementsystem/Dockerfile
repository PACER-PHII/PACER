#Use the maven as "builder" and then use builder to share an artifact to the real tomcat service
FROM maven:3.5.4-jdk-8 as builder
MAINTAINER Mike Riley "michael.riley@gtri.gatech.edu"
COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn package -DskipTests

FROM tomcat:latest
ADD wait-for-it.sh .
COPY --from=builder /usr/src/app/target/JobManagementSystem-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/JobManagementSystem.war
EXPOSE 8080
