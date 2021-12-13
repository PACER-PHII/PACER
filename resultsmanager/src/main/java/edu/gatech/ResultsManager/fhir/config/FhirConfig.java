package edu.gatech.ResultsManager.fhir.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.execution")
@Primary
public class FhirConfig {
	private String dataServiceUri;
	private String dataUser;
	private String dataPass;
	private String epicClientId;
	
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

	public String getEpicClientId() {
		return epicClientId;
	}

	public void setEpicClientId(String epicClientId) {
		this.epicClientId = epicClientId;
	}
}
