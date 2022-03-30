package gatech.edu.JobManagementSystem.service;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import ca.uhn.fhir.rest.gclient.ICriterion;
import ca.uhn.fhir.rest.gclient.TokenClientParam;
import ca.uhn.fhir.rest.param.TokenParam;

@Service
public class PatientIdentifierService {
	Logger log = LoggerFactory.getLogger(PatientIdentifierService.class);
	
	@Autowired
	private FhirContext ctxR4;
	
	@Autowired
	private IParser fhirJsonParserR4;
	
	@Autowired
	private NLPaaSServiceProperties properties;
	
	public String getFhirIdByIdentifier(String identifier) throws Exception {
		String fhirEndpointURL = properties.getFhirEndpointURL();
		log.debug("requesting patient identifier @ fhir server:"+fhirEndpointURL);
		IGenericClient client = ctxR4.newRestfulGenericClient(fhirEndpointURL);
		assignAuthorizationHeader(client, properties);
		ICriterion<TokenClientParam> identifierCriteria = null;
		identifierCriteria = createIDFromString(identifier);
		Bundle results = client
				.search()
				.forResource(Patient.class)
				.where(identifierCriteria)
				.returnBundle(Bundle.class)
				.execute();
		if(!results.hasEntry())
			throw new Exception("Patient identifier " + identifier + " not found.");
		Resource resource = results.getEntryFirstRep().getResource();
		if (resource instanceof OperationOutcome) {
			log.error("Searching for Patient identifier results in an OperationOutcome");
			log.error(fhirJsonParserR4.encodeResourceToString(resource));
			throw new Exception("Cannot find the patient id from identifier");
		}
		Patient patient = (Patient) resource;
		return patient.getIdElement().getIdPart();
	}
	
	public ICriterion<TokenClientParam> createIDFromString(String input) throws StringIndexOutOfBoundsException{
		String identifier = input.substring(input.indexOf('|') + 1);
		String system = input.substring(0, input.indexOf('|'));
		ICriterion<TokenClientParam> returnCriteria = new TokenClientParam("identifier").exactly().systemAndIdentifier(system, identifier);
		return returnCriteria;
	}
	
	public IGenericClient assignAuthorizationHeader(IGenericClient client, NLPaaSServiceProperties properties) {
		if(properties.getFhirEndpointAuthType().equalsIgnoreCase("Bearer")) {
			BearerTokenAuthInterceptor interceptor = new BearerTokenAuthInterceptor();
			interceptor.setToken(properties.getFhirEndpointAuthToken());
			client.registerInterceptor(interceptor);
		}
		else if(properties.getFhirEndpointAuthType().equalsIgnoreCase("Basic")) {
			BasicAuthInterceptor interceptor = new BasicAuthInterceptor(properties.getFhirEndpointAuthBasicUsername(), properties.getFhirEndpointAuthBasicPassword());
			client.registerInterceptor(interceptor);
		}
		return client;
	}
}
