R4 Upgrades

1) CQL parsing for concepts
    * Get CQL.g4 file for ANTLR
    * Configure ANTLR as a dependency for ResultsManager
    * Read the concept definitions
    * Store into memory in the service
2) R4 Direct query to the FHIR Service
    * Replicate every search from ECR.cql
    * Replicate FHIRtoECR logic
3) Code return updates from results
    * Based on the concept search used, write matching code
    * Get concept from ANTLR
    * Compare values and systems. Transform into value systems and codes
    * Turn into ECR CodeableConcepts with system, value, and displays
4) Python mock service
    * Rebuild mock service with FHIR queries from OCHIN
    