package edu.gatech.ResultsManager.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.DirectFhirECRCreator;
import edu.gatech.ResultsManager.cql.execution.service.CQLExecutionService;
import edu.gatech.ResultsManager.cql.processing.service.CQLConceptCaptureService;
import edu.gatech.ResultsManager.cql.storage.service.CQLStorageService;
import edu.gatech.ResultsManager.fhir.identifier.service.PatientIdentifierService;
import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.TypeableID;

@RestController
public class ResultsManagerController{
	Logger log = LoggerFactory.getLogger(ResultsManagerController.class);

	CQLStorageService cqlStorageService;
	PatientIdentifierService patientIdentifierService;
	CQLExecutionService cqlExecutionService;
	CQLConceptCaptureService cqlConceptCaptureService;
	DirectFhirECRCreator directFhirECRCreator;

	@Autowired
	public ResultsManagerController(CQLStorageService cqlStorageService, PatientIdentifierService patientIdentifierService, CQLExecutionService cqlExecutionService,
			CQLConceptCaptureService cqlConceptCaptureService, DirectFhirECRCreator directFhirECRCreator) {
		super();
		this.cqlStorageService = cqlStorageService;
		this.patientIdentifierService = patientIdentifierService;
		this.cqlExecutionService = cqlExecutionService;
		this.cqlConceptCaptureService = cqlConceptCaptureService;
		this.directFhirECRCreator = directFhirECRCreator;
	}
	
	@RequestMapping(value = "/case", method = RequestMethod.POST)
	public ResponseEntity<ECR> pacerFlow(
			@RequestParam(value =  "firstName", required = false, defaultValue = "") String firstName,
			@RequestParam(value =  "lastName", required = false, defaultValue = "") String lastName,
			@RequestParam(value = "identifier", required = true, defaultValue = "") String identifier,
			@RequestParam(value = "cqlType", required = true, defaultValue = "") String cqlType,
			@RequestParam(value = "ecrId", required = false, defaultValue = "") String ecrId,
			@RequestParam(value = "labOrderDate", required = false, defaultValue = "1970-01-01") @DateTimeFormat(pattern="yyyy-MM-dd") Date labOrderDate){
		log.debug("*-* Received POST request on /case");
		log.debug("*-* Request param firstName:" + firstName);
		log.debug("*-* Request param lastName:" + lastName);
		log.debug("*-* Request param identifier:" + identifier);
		log.debug("*-* Request param cqlType:" + cqlType);
		log.debug("*-* Request param ecrId:" + ecrId);
		log.debug("*-* Request param labOrderName:" + labOrderDate);
		//Setup base ecr response
		ECR ecr = new ECR();
		if(ecrId != null && !ecrId.isEmpty()) {
			ecr.setECRId(ecrId);
		}
		//Set identifier if the identifier is not empty
		if(identifier != null && !identifier.isEmpty()){
			TypeableID originalIdentifier = new TypeableID();
			String[] identifierParts = identifier.split("\\|"); 
			if(identifierParts.length == 2) {
				originalIdentifier.settype(identifierParts[0]);
				originalIdentifier.setvalue(identifierParts[1]);
			}
			else {
				originalIdentifier.setvalue(identifierParts[0]);
			}
			ecr.getPatient().getid().add(originalIdentifier);
		}
		//Retrieve patient id from identifier
		String patientId;
		String cqlBody;
		try {
			patientId = patientIdentifierService.getFhirIdByIdentifier(identifier);
			log.debug("patientId from patientIdentifierServer:"+patientId);
			//retrieve cql from cqlstorage
			cqlBody = cqlStorageService.requestCQL(cqlType);
			//Create CQLConceptCapture for matching codeableconcepts in results
			cqlConceptCaptureService.processCQL(cqlBody);
			ECR ecrDirect = directFhirECRCreator.queryFhirServerAndCreateECR(patientId);
			ecr.update(ecrDirect);
		} catch (Exception e) {
			log.error(e.getMessage());
			return new ResponseEntity<ECR>(ecr,HttpStatus.OK);
		}
		//ecrStorageService.storeECR(ecr.toString());
		return new ResponseEntity<ECR>(ecr,HttpStatus.OK);
	}

	public CQLStorageService getCqlStorageService() {
		return cqlStorageService;
	}

	public void setCqlStorageService(CQLStorageService cqlStorageService) {
		this.cqlStorageService = cqlStorageService;
	}

	public PatientIdentifierService getPatientIdentifierService() {
		return patientIdentifierService;
	}

	public void setPatientIdentifierService(PatientIdentifierService patientIdentifierService) {
		this.patientIdentifierService = patientIdentifierService;
	}

	public CQLExecutionService getCqlExecutionService() {
		return cqlExecutionService;
	}

	public void setCqlExecutionService(CQLExecutionService cqlExecutionService) {
		this.cqlExecutionService = cqlExecutionService;
	}
}
