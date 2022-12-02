package edu.gatech.ResultsManager.cql.execution.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.gatech.ResultsManager.FHIR2ECR.service.CQLFHIR2ECRService;

@Service
@Configuration
@ConfigurationProperties(prefix="cql.execution")
@Primary
public class CQLExecutionService {
	Logger log = LoggerFactory.getLogger(CQLExecutionService.class);
	
	private String endpoint;
	private String terminologyServiceUri;
	private String dataServiceUri;
	private String terminologyUser;
	private String terminologyPass;
	private String dataUser;
	private String dataPass;
	private String codeMapperServiceUri;
	private String codeMapperUser;
	private String codeMapperPass;
	private ObjectNode codeMapperSystemsMap;
	private String epicClientId;
	private RestTemplate restTemplate;
	private ObjectMapper objectMapper;
	private ObjectNode requestJson;
	
	public CQLExecutionService() {
		restTemplate = new RestTemplateBuilder().setReadTimeout(Duration.ofMinutes(5)).build();
		objectMapper = new ObjectMapper();
		requestJson = JsonNodeFactory.instance.objectNode();
	}

	public JsonNode evaluateCQL(String cqlBody, String patientId, Date labOrderDate) {
		UriComponents uriComponents = UriComponentsBuilder.newInstance()
				.scheme("http").host(endpoint).port("8080").path("/cql/evaluate").build();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		if(epicClientId != null && !epicClientId.isEmpty()) {
			headers.add("Epic-Client-ID", epicClientId);
		}
		requestJson.put("terminologyServiceUri", terminologyServiceUri);
		requestJson.put("dataServiceUri", dataServiceUri);
		requestJson.put("terminologyUser", terminologyUser);
		requestJson.put("terminologyPass", terminologyPass);
		requestJson.put("dataUser", dataUser);
		requestJson.put("dataPass", dataPass);
		requestJson.put("codeMapperServiceUri", codeMapperServiceUri);
		requestJson.put("codeMapperUser", codeMapperUser);
		requestJson.put("codeMapperPass", codeMapperPass);
		requestJson.put("codeMapperSystemsMap", codeMapperSystemsMap);
		requestJson.put("patientId", patientId);
		if(labOrderDate != null) {
			String labOrderDateString = new SimpleDateFormat("yyyy-MM-dd").format(labOrderDate);
			requestJson.put("labOrderDate", labOrderDateString);
		}
		requestJson.put("code", cqlBody);
		HttpEntity<String> entity = new HttpEntity<String>(requestJson.toString(), headers);
		log.debug("*-* requesting cql execution at:"+uriComponents.toUriString());
		for(Iterator<Entry<String, JsonNode>> jsonFields = requestJson.fields();
			    jsonFields.hasNext();) {
			Entry<String, JsonNode> keyValue = jsonFields.next();
			//We don't have to reshare the cql body every time
			if(!keyValue.getKey().equalsIgnoreCase("code")){
				log.debug("*-* cql execution request param: " + keyValue.getKey() + ":::" + keyValue.getValue().asText());
			}
		}
		String cQLResultString = restTemplate.postForEntity(uriComponents.toUriString(), entity, String.class).getBody();
		JsonNode resultsJson = null;
		try {
			resultsJson = objectMapper.readTree(cQLResultString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultsJson;
	}
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getTerminologyServiceUri() {
		return terminologyServiceUri;
	}

	public void setTerminologyServiceUri(String fhirServiceUri) {
		this.terminologyServiceUri = fhirServiceUri;
	}

	public String getDataServiceUri() {
		return dataServiceUri;
	}

	public void setDataServiceUri(String dataServiceUri) {
		this.dataServiceUri = dataServiceUri;
	}

	public String getTerminologyUser() {
		return terminologyUser;
	}

	public void setTerminologyUser(String terminologyUser) {
		this.terminologyUser = terminologyUser;
	}

	public String getTerminologyPass() {
		return terminologyPass;
	}

	public void setTerminologyPass(String terminologyPass) {
		this.terminologyPass = terminologyPass;
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

	public String getCodeMapperServiceUri() {
		return codeMapperServiceUri;
	}

	public void setCodeMapperServiceUri(String codeMapperServiceUri) {
		this.codeMapperServiceUri = codeMapperServiceUri;
	}

	public String getCodeMapperUser() {
		return codeMapperUser;
	}

	public void setCodeMapperUser(String codeMapperUser) {
		this.codeMapperUser = codeMapperUser;
	}

	public String getCodeMapperPass() {
		return codeMapperPass;
	}

	public void setCodeMapperPass(String codeMapperPass) {
		this.codeMapperPass = codeMapperPass;
	}

	public ObjectNode getCodeMapperSystemsMap() {
		return codeMapperSystemsMap;
	}

	public void setCodeMapperSystemsMap(String codeMapperSystemsMap) {
		try {
			this.codeMapperSystemsMap = (ObjectNode) objectMapper.readTree(codeMapperSystemsMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getEpicClientId() {
		return epicClientId;
	}

	public void setEpicClientId(String epicClientId) {
		this.epicClientId = epicClientId;
	}
	
}
