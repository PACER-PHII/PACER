package edu.gatech.ResultsManager.fhir.identifier.service;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.composite.BaseIdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.gclient.TokenClientParam;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.execution")
@Primary
public class PatientIdentifierService {
	private String dataServiceUri;
	private String dataUser;
	private String dataPass;
	private IGenericClient client;
	private FhirContext ctx;
	public PatientIdentifierService() {
		ctx = FhirContext.forDstu3();
	}
	
	public String getFhirIdByIdentifier(String identifier) throws Exception {
		client = ctx.newRestfulGenericClient(dataServiceUri);
		BaseIdentifierDt identifierObject = createIDFromString(identifier);
		if(dataUser != null && !dataUser.isEmpty() && dataPass != null && !dataPass.isEmpty()) {
			client.registerInterceptor(new BasicAuthInterceptor(dataUser,dataPass));
		}
		Bundle results = client
				.search()
				.forResource(Patient.class)
				.where(new TokenClientParam("identifier").exactly().identifier(identifierObject))
				.returnBundle(Bundle.class)
				.execute();
		if(!results.hasEntry())
			throw new Exception("Patient identifier " + identifier + "not found.");
		Patient patient = (Patient) results.getEntryFirstRep().getResource();
		return patient.getIdElement().getIdPart();
	}
	
	public String getDataServiceUri() {
		return dataServiceUri;
	}

	public void setDataServiceUri(String dataServiceUri) {
		this.dataServiceUri = dataServiceUri;
	}

	public String getDataUser() {
		return dataUser;
	}

	public void setDataUser(String dataUser) {
		this.dataUser = dataUser;
	}

	public String getDataPass() {
		return dataPass;
	}

	public void setDataPass(String dataPass) {
		this.dataPass = dataPass;
	}
	
	public BaseIdentifierDt createIDFromString(String input) {
		String code = input.substring(0, input.indexOf('|'));
		String system = input.substring(input.indexOf('|') + 1);
		BaseIdentifierDt identifier = new IdentifierDt(system,code);
		return identifier;
	}
}
