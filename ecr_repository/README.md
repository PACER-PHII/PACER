# PHCR Controller

This project is a prototype PHCR_Controller for the CDC-STI project. It manages a database of Electronic Case Records (ECRs) and allows for iterative updates

## Requirements

PHCR_Controller is written using annotation based springframework-boot, and uses a postgres database backend. We use maven to compile the project, and deploy the war artifact in either tomcat 7 or tomcat 8.

## Installing

Please ensure that maven and java-jdk 7.0 or higher is installed

```
sudo apt-get install -y java-jdk-8 maven
```

FHIR_Controller relies on a custom common library called ecr_javalib that must also be built
```
cd ecr_javalib
mvn clean install -DskipTests
```

Once the common library is built, compile the main project.
```
cd ..
mvn clean install -DskipTests
```

To deploy the project, copy the war artifact into your tomcat webapp directoy
```
cp target/PHCR_Controller-0.0.1-SNAPSHOT.war $CATALINA_BASE/webapps
```

## Configuring

If you are handling network configuration manually, you must edit the main configuration file found at src/main/resources/application.properties
```
#These are the connection information to the database that contains the ECRJob information
spring.datasource.url = jdbc:postgresql://localhost:5432/OMOP # This must change to your local database 
spring.datasource.username = postgres
spring.datasource.password = postgres
spring.datasource.driver-class-name = org.postgresql.Driver
spring.jpa.hibernate.ddl-auto = validate
spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect

#Some common logging package spaces
logging.level.gatech.edu = debug
logging.level.gatech.edu.PHCR_Controller.controller = debug

#The context path for the web server, such as http://www.myserver.com/PHCRController
server.contextPath = /PHCRController

#A Seperate direct connection to handle direct DB function commands seperate from the Spring framework. Should mirror spring.datasource properties
direct.datasource.url = jdbc:postgresql://localhost:5432/OMOP
direct.datasource.username=postgres
direct.datasource.password=postgres

```

### REST API

In order to request an ECR record you call a ECR resource ENDPOINT
```
GET http://www.fhircontroller.com/ECR?id=1
```
The response will contain an ECR object in the response body in the form of application/json.

You can request other ECR resources via different search criteria: firstname, lastname, zipCode and diagnosisCode

```
GET http://www.fhircontroller.com/ECR?firstname=Abraham
GET http://www.fhircontroller.com/ECR?lastname=Lincoln
GET http://www.fhircontroller.com/ECR?zipcode=42748
GET http://www.fhircontroller.com/ECR?diagnosisCode=78563
```

In order to write a new ECR record, simply POST to the same endpoint. If you would like to update an endpoint, use the PUT action and include the ECR id as well. 
