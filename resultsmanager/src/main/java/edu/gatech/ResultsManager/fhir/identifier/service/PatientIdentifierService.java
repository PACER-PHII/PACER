package edu.gatech.ResultsManager.fhir.identifier.service;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import edu.gatech.ResultsManager.controller.ResultsManagerController;
import edu.gatech.ResultsManager.fhir.config.FhirConfig;

@Service
@Primary
public class PatientIdentifierService {
	Logger log = LoggerFactory.getLogger(PatientIdentifierService.class);
	private FhirConfig fhirConfig;
	private FhirContext ctx;
	private IGenericClient client;
	
	@Autowired
	public PatientIdentifierService(FhirConfig fhirConfig) {
		this.fhirConfig = fhirConfig;
		ctx = FhirContext.forR4();
	}
	
	public String getFhirIdByIdentifier(String identifier) throws Exception {
		log.debug("*-* request patient identifier @ fhir server:"+fhirConfig.getDataServiceUri());
		client = ctx.newRestfulGenericClient(fhirConfig.getDataServiceUri());
		BaseIdentifierDt identifierObject = createIDFromString(identifier);
		if(fhirConfig.getDataUser() != null && !fhirConfig.getDataUser().isEmpty() && fhirConfig.getDataPass() != null && !fhirConfig.getDataPass().isEmpty()) {
			client.registerInterceptor(new BasicAuthInterceptor(fhirConfig.getDataUser(),fhirConfig.getDataPass()));
		}
		if(fhirConfig.getEpicClientId() != null && !fhirConfig.getEpicClientId().isEmpty()) {
			AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
			interceptor.addHeaderValue("Epic-Client-Id", fhirConfig.getEpicClientId());
			client.registerInterceptor(interceptor);
		}
		Bundle results = client
				.search()
				.forResource(Patient.class)
				.where(new TokenClientParam("identifier").exactly().identifier(identifierObject))
				.returnBundle(Bundle.class)
				.execute();
		if(!results.hasEntry())
			throw new Exception("Patient identifier " + identifier + "not found.");
		Resource resource = results.getEntryFirstRep().getResource();
		if (resource instanceof OperationOutcome) {
			log.error("Searching for Patient identifier results in an OperationOutcome");
			log.error(ctx.newJsonParser().encodeResourceToString(resource));
			throw new Exception("Cannot find the patient id from identifier");
		}
		Patient patient = (Patient) resource;
		return patient.getIdElement().getIdPart();
	}
	
	public BaseIdentifierDt createIDFromString(String input) {
		String code = input.substring(input.indexOf('|') + 1);
		String system = input.substring(0, input.indexOf('|'));
		BaseIdentifierDt identifier = new IdentifierDt(system,code);
		return identifier;
	}
}
