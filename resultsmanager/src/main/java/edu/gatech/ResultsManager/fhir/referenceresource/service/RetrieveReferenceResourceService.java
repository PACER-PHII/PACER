package edu.gatech.ResultsManager.fhir.referenceresource.service;

import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationAdministration;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.exceptions.FHIRException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import edu.gatech.ResultsManager.fhir.config.FhirConfig;

@Service
@Primary
public class RetrieveReferenceResourceService {
	Logger log = LoggerFactory.getLogger(RetrieveReferenceResourceService.class);
	private FhirConfig fhirConfig; 
	private IGenericClient client;
	private FhirContext ctx;
	
	@Autowired
	public RetrieveReferenceResourceService(FhirConfig fhirConfig) {
		this.fhirConfig = fhirConfig;
		ctx = FhirContext.forDstu3();
		client = generateClient();
	}
	
	public Medication getRelatedMedication(MedicationAdministration medAdmin) throws FHIRException {
		Reference medRef = medAdmin.getMedicationReference();
		log.debug("*-* read related medication:"+medRef.getReferenceElement().toString()+" related to medication administration:"+medAdmin.getId());
		Medication returnMed = client
		.read()
		.resource(Medication.class)
		.withId(medRef.getReferenceElement().getIdPart())
		.execute();
		return returnMed;
	}
	
	public Medication getRelatedMedication(MedicationRequest medRequest) throws FHIRException {
		Reference medRef = medRequest.getMedicationReference();
		log.debug("*-* read related medication:"+medRef.getReferenceElement().toString()+" related to medication request:"+medRequest.getId());
		Medication returnMed = client
		.read()
		.resource(Medication.class)
		.withId(medRef.getReferenceElement().getIdPart())
		.execute();
		return returnMed;
	}
	
	public Medication getRelatedMedication(MedicationStatement medStatement) throws FHIRException {
		Reference medRef = medStatement.getMedicationReference();
		log.debug("*-* read related medication:"+medRef.getReferenceElement().toString()+" related to medication statement:"+medStatement.getId());
		Medication returnMed = client
		.read()
		.resource(Medication.class)
		.withId(medRef.getReferenceElement().getIdPart())
		.execute();
		return returnMed;
	}
	
	protected IGenericClient generateClient() {
		client = ctx.newRestfulGenericClient(fhirConfig.getDataServiceUri());
		if(fhirConfig.getDataUser() != null && !fhirConfig.getDataUser().isEmpty() && fhirConfig.getDataPass() != null && !fhirConfig.getDataPass().isEmpty()) {
			client.registerInterceptor(new BasicAuthInterceptor(fhirConfig.getDataUser(),fhirConfig.getDataPass()));
		}
		if(fhirConfig.getEpicClientId() != null && !fhirConfig.getEpicClientId().isEmpty()) {
			AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
			interceptor.addHeaderValue("Epic-Client-Id", fhirConfig.getEpicClientId());
			client.registerInterceptor(interceptor);
		}
		return client;
	}
	
}
