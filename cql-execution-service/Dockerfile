FROM maven:3.6.0-jdk-8 AS builder
WORKDIR /usr/src/cql_src
ADD . .
#RUN git clone https://github.com/DBCG/cql_engine.git Using local copy now
RUN mvn clean install -DskipTests -f /usr/src/cql_src/cql_engine/
RUN mvn clean install -DskipTests -f /usr/src/cql_src

FROM jetty:9.4.32-jdk8
#move the WAR from builder into jetty container
COPY --from=builder /usr/src/cql_src/target/CQLExecSvc-1.0-SNAPSHOT.war /var/lib/jetty/webapps/cql.war
COPY --from=builder /usr/src/cql_src/src/main/resources/* /var/lib/jetty/resources/
EXPOSE 8080
