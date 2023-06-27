package edu.gatech.ResultsManager.cql.storage.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.gatech.ResultsManager.controller.ResultsManagerController;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.storage")
@Primary
public class CQLStorageService {
	Logger log = LoggerFactory.getLogger(CQLStorageService.class);
	
	private String endpoint;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	
	public CQLStorageService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	public String requestCQL(String cqlName) {
		URL baseUrl;
		URL finalUrl;
		try {
			baseUrl = new URL(endpoint);
			finalUrl = new URL(baseUrl, "CQL?name="+cqlName);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		log.debug("*-* requesting to cql storage service at:" + finalUrl.toString());
		String responseString = restTemplate.getForEntity(finalUrl.toString(), String.class).getBody();
		ObjectNode responseObject = null;
		try {
			responseObject = (ObjectNode) objectMapper.readTree(responseString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		String cqlString = responseObject.get("_embedded").get("CQLs").get(0).get("body").asText();
		return cqlString;
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
