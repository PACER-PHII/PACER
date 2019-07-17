#Build the Maven project
FROM maven:3.5.2-alpine as builder

COPY . /usr/src/app
WORKDIR /usr/src/app
RUN mvn install

#Build the Tomcat container
FROM tomcat:alpine
#set environment variables below and uncomment the line. Or, you can manually set your environment on your server.
#ENV JDBC_URL=jdbc:postgresql://<host>:<port>/<database> JDBC_USERNAME=<username> JDBC_PASSWORD=<password>
ADD wait-for-it.sh .
RUN apk update
RUN apk add zip postgresql-client

# Copy GT-FHIR war file to webapps.
COPY --from=builder /usr/src/app/target/omoponfhir-omopv5-stu3-conceptmapping-server.war $CATALINA_HOME/webapps/omoponfhir-conceptmapping.war

EXPOSE 8080
