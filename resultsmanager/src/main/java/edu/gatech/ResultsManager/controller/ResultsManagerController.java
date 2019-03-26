package edu.gatech.ResultsManager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.CQLFHIR2ECRService;
import edu.gatech.ResultsManager.cql.execution.service.CQLExecutionService;
import edu.gatech.ResultsManager.cql.storage.service.CQLStorageService;
import edu.gatech.ResultsManager.ecr.storage.service.ECRStorageService;
import edu.gatech.ResultsManager.fhir.identifier.service.PatientIdentifierService;
import gatech.edu.STIECR.JSON.ECR;

@RestController
public class ResultsManagerController{
	Logger log = LoggerFactory.getLogger(ResultsManagerController.class);

	CQLStorageService cqlStorageService;
	PatientIdentifierService patientIdentifierService;
	CQLExecutionService cqlExecutionService;
	//ECRStorageService ecrStorageService;
	CQLFHIR2ECRService cqlFhir2EcrService;
	
	@Autowired
	public ResultsManagerController(CQLStorageService cqlStorageService, PatientIdentifierService patientIdentifierService, CQLExecutionService cqlExecutionService,
			CQLFHIR2ECRService cqlFhir2EcrService) {
		super();
		this.cqlStorageService = cqlStorageService;
		this.patientIdentifierService = patientIdentifierService;
		this.cqlExecutionService = cqlExecutionService;
		this.cqlFhir2EcrService = cqlFhir2EcrService;
	}
	
	@RequestMapping(value = "/case", method = RequestMethod.POST)
	public ResponseEntity<ECR> pacerFlow(
			@RequestParam(value =  "firstName", required = false) String firstName,
			@RequestParam(value =  "lastName", required = false) String lastName,
			@RequestParam(value = "identifier", required = true) String identifier,
			@RequestParam(value = "cqlType", required = true) String cqlName){
		String cqlBody = cqlStorageService.requestCQL(cqlName);
		//ECR ecr = ecrStorageService.getECR(firstName, lastName);
		ECR ecr = new ECR();
		String patientId;
		try {
			patientId = patientIdentifierService.getFhirIdByIdentifier(identifier);
		} catch (Exception e) {
			return new ResponseEntity<ECR>(ecr,HttpStatus.UNPROCESSABLE_ENTITY);
		}
		log.debug("patientId:"+patientId);
		JsonNode cqlResults = cqlExecutionService.evaluateCQL(cqlBody,patientId);
		ECR ecrFromCQL = cqlFhir2EcrService.CQLFHIRResultsToECR((ArrayNode)cqlResults);
		ecr.update(ecrFromCQL);
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

	public CQLFHIR2ECRService getCqlFhir2EcrService() {
		return cqlFhir2EcrService;
	}

	public void setCqlFhir2EcrService(CQLFHIR2ECRService cqlFhir2EcrService) {
		this.cqlFhir2EcrService = cqlFhir2EcrService;
	}
}
