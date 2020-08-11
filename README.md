# PACER
PACER software and deployment instruction
## What is PACER
PACER is a stand-alone deployment service-stack designed to help FHIR Health Record Providers manage access to their data, and facilitate automatic report submission to external sources.
PACER exists as a set of microservices that live within the same network as the FHIR provider. By managing access to the FHIR server through the PACER microservices, maintainers can provide a stronger level of access and security to their secure data sources.
The system has 2 major configurable feature points: a Clinical Query Language (CQL) library for crafting patient context, and fhir-filter library for restricting access to certain resources and data, for the purpose of securing Personally Indentifying Information. Both libraries can be configured at boot-up and run-time, and will be explained in detail in their subsequent wiki sections. Please refer to the wiki for more detailed explanations on their components at https://github.com/gt-health/PACER/wiki
Software requirements
* Docker-CE (Community Edition). Refer to https://docs.docker.com/install/ for installation instructions
* Docker-Compose, version 1.3 or later. Refer to https://docs.docker.com/compose/install/ for installation instructions.

## Configuring PACER for your environment
Before installing PACER you need to configure a few entries in order to point PACER to your data sources.
In the ```docker-compose.yml``` file...
* Update the ```CQL_EXECUTION_DATA_SERVICE``` to point to your base FHIR URL.
* If your FHIR Service uses authentication, update the ```CQL_EXECUTION_DATA_USER``` and ```CQL_EXECUTION_DATA_USER``` to the basic authentication credentials you would like to use.
* If you have a FHIR terminology service, or if you rown FHIR server acts as a terminology service, update the ```CQL_EXECUTION_TERMINOLOGY_SERVICE``` to a different base FHIR URL.
* Additionally, if you choose to use a seperate terminology service, you must either update or remove the ```CQL_EXECUTION_TERMINOLOGY_USER``` and ```CQL_EXECUTION_TERMINOLOGY_PASS``` credentials
  * If you are using a fhir service with a **ConceptMap/$translate** operation, update ```MAPPING_TERMINOLOGY_URL``` to the fhir server's base plus the /ConceptMap/ resource URL component, as shown in the placeholder value.
  * In addition, update the ```AUTH_TYPE``` environment variable with either new Basic credentials in form of **Basic $username:$password**, or leave empty to disable authentication to the translation service.
  * Finally, you must also update the ```CQL_EXECUTION_CODEMAPPER_USER``` and ```CQL_EXECUTION_CODEMAPPER_PASS``` to the basic credentials as well.
  * In order to define the actual mapping, you must create a new flat-key json object in the ```CQL_EXECUTION_CODEMAPPER_SYSTEMS_MAP``` variable. Each key is a source system, and each value is the target system by which it will be mapped too. In the example, *http://www.nlm.nih.gov/research/umls/rxnorm* is the **source** and *http://hl7.org/fhir/ndfrt* is the **target**.
## Installation Instructions
* From the command line, run ```docker-compose build``` from the top of the project. This will build from the source folders deployable images for 6 different containers: cql-storage, cql-execution, db, fhir-filter, job-management-system, results-manager.
* From the command line, run ```docker-compose up -d``` This will turn on all the components and detach the process from the command line.
