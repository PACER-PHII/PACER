FROM maven:3.6.0-jdk-8 AS builder
WORKDIR /usr/src/resultsmanager_src
ADD . .
RUN mvn clean install -DskipTests -f /usr/src/resultsmanager_src/ecr_javalib
RUN mvn clean install -DskipTests -f /usr/src/resultsmanager_src/

FROM tomcat:10-jdk8
#move the WAR for contesa to the webapps directory
COPY --from=builder /usr/src/resultsmanager_src/target/ResultsManager-0.0.1-SNAPSHOT.war /usr/local/tomcat/webapps/ResultsManager.war
COPY --from=builder /usr/src/resultsmanager_src/src/main/resources/* /usr/local/tomcat/src/main/resources/
