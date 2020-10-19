package org.opencds.cqf.cql.util.service;

import java.io.IOException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OAuthTokenService {
	public static final String RESOURCE_PATH = "/token";
	public RestTemplate restTemplate;
	
	public OAuthTokenService() {
		this.restTemplate = new RestTemplate();
	}
	
	public String getAccessTokenFromOAuth(String host, String client_id, String client_secret, String redirect_uri, String code) {
		if(host.isEmpty()) {
			return "";
		}
		String tokenPath = host + RESOURCE_PATH;
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(tokenPath);
		builder.queryParam("grant_type", "authorization_code");
		if(!client_id.isEmpty()) {
			builder.queryParam("client_id", client_id);
		}
		if(!client_secret.isEmpty()) {
			builder.queryParam("client_secret", client_secret);
		}
		if(!redirect_uri.isEmpty()) {
			builder.queryParam("redirect_uri", redirect_uri);
		}
		if(!code.isEmpty()) {
			builder.queryParam("code", code);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.add("accept", "application/json");
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity<String> postEntity = new HttpEntity<String>("", headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, postEntity, String.class);
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode jsonNode = mapper.readTree(responseEntity.getBody());
			return jsonNode.get("access_token").asText();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
