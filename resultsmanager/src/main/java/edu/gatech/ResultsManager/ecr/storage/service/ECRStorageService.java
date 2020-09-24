package edu.gatech.ResultsManager.ecr.storage.service;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import gatech.edu.STIECR.JSON.ECR;

@Service
@Configuration
@ConfigurationProperties(prefix="ecr.storage")
@Primary
public class ECRStorageService {
	private String endpoint;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	
	public ECRStorageService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	public String storeECR(String ecrBody) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("https").host(endpoint).port("443").path("/ECR").build();
		String cqlString = restTemplate.postForEntity(uriComponents.toUriString(), ecrBody, String.class).getBody();
		return cqlString;
	}
	
	public ECR getECR(String firstName,String lastName) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("https").host(endpoint).port("443").path("/ECR").queryParam("firstName", firstName).queryParam("lastName", lastName).build();
		List<ECR> ecrList = restTemplate.getForEntity(uriComponents.toUriString(), List.class).getBody();
		return ecrList.get(0);
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
