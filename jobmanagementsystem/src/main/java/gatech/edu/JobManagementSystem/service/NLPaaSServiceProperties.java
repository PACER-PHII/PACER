package gatech.edu.JobManagementSystem.service;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix="nlpaas")
public class NLPaaSServiceProperties {
	public String endpointURL;
	public Map<String, List<String>> serviceJobs;
	public String fhirEndpointURL;
	public String fhirEndpointAuthType;
	public String fhirEndpointAuthBasicUsername;
	public String fhirEndpointAuthBasicPassword;
	public String fhirEndpointAuthToken;
	public String customJobPostBody;
	
	public String getEndpointURL() {
		return endpointURL;
	}

	public void setEndpointURL(String endpointURL) {
		this.endpointURL = endpointURL;
	}
	
	public Map<String, List<String>> getServiceJobs() {
		return serviceJobs;
	}

	public void setServiceJobs(Map<String, List<String>> serviceJobs) {
		this.serviceJobs = serviceJobs;
	}
	
	public String getFhirEndpointURL() {
		return fhirEndpointURL;
	}

	public void setFhirEndpointURL(String fhirEndpointURL) {
		this.fhirEndpointURL = fhirEndpointURL;
	}

	public String getFhirEndpointAuthType() {
		return fhirEndpointAuthType;
	}

	public void setFhirEndpointAuthType(String fhirEndpointAuthType) {
		this.fhirEndpointAuthType = fhirEndpointAuthType;
	}

	public String getFhirEndpointAuthToken() {
		return fhirEndpointAuthToken;
	}

	public void setFhirEndpointAuthToken(String fhirEndpointAuthToken) {
		this.fhirEndpointAuthToken = fhirEndpointAuthToken;
	}

	public String getCustomJobPostBody() {
		return customJobPostBody;
	}

	public void setCustomJobPostBody(String customJobPostBody) {
		this.customJobPostBody = customJobPostBody;
	}

	public String getFhirEndpointAuthBasicUsername() {
		return fhirEndpointAuthBasicUsername;
	}

	public void setFhirEndpointAuthBasicUsername(String fhirEndpointAuthBasicUsername) {
		this.fhirEndpointAuthBasicUsername = fhirEndpointAuthBasicUsername;
	}

	public String getFhirEndpointAuthBasicPassword() {
		return fhirEndpointAuthBasicPassword;
	}

	public void setFhirEndpointAuthBasicPassword(String fhirEndpointAuthBasicPassword) {
		this.fhirEndpointAuthBasicPassword = fhirEndpointAuthBasicPassword;
	}

	@PostConstruct
	public void resolvePropertiesAfterInitiation() {
		//Update the customJobPostBody with annotated configuration at buildtime where possible
		customJobPostBody = customJobPostBody.replace("${fhir_endpoint_url}", fhirEndpointURL);
		customJobPostBody = customJobPostBody.replace("${fhir_auth_type}", fhirEndpointAuthType);
		customJobPostBody = customJobPostBody.replace("${fhir_auth_basic_username}", fhirEndpointAuthBasicUsername);
		customJobPostBody = customJobPostBody.replace("${fhir_auth_basic_password}", fhirEndpointAuthBasicPassword);
		customJobPostBody = customJobPostBody.replace("${fhir_auth_token}", fhirEndpointAuthToken);
		
	}
}
