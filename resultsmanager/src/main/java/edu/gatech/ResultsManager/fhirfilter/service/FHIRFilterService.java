package edu.gatech.ResultsManager.fhirfilter.service;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.CQLFHIR2ECRService;
import gatech.edu.STIECR.JSON.ECR;

@Service
@Configuration
@ConfigurationProperties(prefix="fhir.filter")
@Primary
public class FHIRFilterService {
	Logger log = LoggerFactory.getLogger(FHIRFilterService.class);
	
	private String endpoint;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	
	public FHIRFilterService() {
		restTemplate = new RestTemplate();
		objectMapper = new ObjectMapper();
	}

	/**
	 * 
	 * @param rawFhir A FHIR Resource converted into string
	 * @return A filtered version of the FHIR bundle. NOTE: must convert back to structured data if necessary
	 */
	public String applyFilter(String rawFhir,boolean escaped) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("http").host(endpoint).port("8080").path("/fhirfilter/apply").build();
		log.debug("*-* requesting fhir filter @:"+uriComponents.toUriString());
		if(!escaped) { //Trying to prevent double un-escaping here
			rawFhir = StringEscapeUtils.unescapeJava(rawFhir);
		}
		if(rawFhir.charAt(0) == '"' && rawFhir.charAt(rawFhir.length()-1) == '"') {
			//BAD HACK TO GET AROUND VALUE STRING WRAPPING
			rawFhir = rawFhir.substring(1, rawFhir.length()-1);
		}
		log.debug("fhir resource before filter:"+rawFhir);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> entity = new HttpEntity<String>(rawFhir, headers);
		String filteredResult = restTemplate.postForEntity(uriComponents.toUriString(), entity, String.class).getBody();
		log.debug("fhir resource after filter:"+filteredResult);
		return filteredResult;
	}
	
	/**
	 * 
	 * @param fhirJson works with jackson json nodes. If an array node is passed, we break it out into it's children.
	 * @return the same as applyFilter(string)
	 */
	public String applyFilter(JsonNode fhirJson,boolean escaped) {
		JsonNode output;
		if(fhirJson.isArray()) {
			output = JsonNodeFactory.instance.arrayNode();
			for(JsonNode node:(ArrayNode)fhirJson) {
				((ArrayNode)output).add(applyFilter(node.toString(),escaped));
				return output.toString();
			}
		}
		try {
			return applyFilter(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(objectMapper.readValue(fhirJson.toString(), Object.class)),escaped);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	
}
