# CQLValuesetExpander
CQLValuesetExpander is a simple CLI tool that requires a FHIR Terminology server, and a CQL file. It reads all the "Valueset" tags within a CQL file, runs the $expand FHIR command on the valuesets, and creates "codesystem" and "define concept" tags from that expansion.
## CLI Parameters
* ```-input``` or ```-i```: relative path to input CQL file to be read
* ```-output``` or ```-o```: relative path to output CQL snippet to be written
* ```-host``` or ```-h```: host URL FHIR endpoint
* ```-username``` or ```-u```: FHIR endpoint BASIC Auth username
* ```-password``` or ```-p```: FHIR endpoint BASIC Auth password

## Example File
Within ```src/main/resources``` you can find a ```testCQL.cql``` for an example CQL input, and ```output.txt``` to see the CQL snippet generated from this input file