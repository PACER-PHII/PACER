package org.opencds.cqf.cql.util.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CapabilityStatementRequirementsService {
	
	public RestTemplate restTemplate;
	
	public CapabilityStatementRequirementsService() {
		restTemplate = new RestTemplate();
	}

	
	public Map<String, Set<String> > getCapabilityStatementOfFhirServer(String dataServiceUri){
		Map<String, Set<String> > spMap = new HashMap<String, Set<String> >();
		String metadataString = restTemplate.getForEntity(dataServiceUri + "/metadata", String.class).getBody();
		JsonParser parser = new JsonParser();
		
		JsonObject metadataJson = (JsonObject)parser.parse(metadataString);
		JsonArray restArray = (JsonArray) metadataJson.get("rest");
		for(JsonElement restElement:restArray) {
			JsonObject restObject = (JsonObject) restElement;
			JsonArray resourceArray = (JsonArray) restObject.get("resource");
			for(JsonElement resourceElement:resourceArray) {
				JsonObject resourceObject = (JsonObject) resourceElement;
				String resourceName = resourceObject.get("type").getAsString();
				if(!spMap.containsKey("resourceName")) {
					spMap.put(resourceName, new HashSet<String>());
				}
				Set<String> searchParameterSet = spMap.get(resourceName);
				if(resourceObject.keySet().contains("searchParam")) {
					JsonArray searchArray = (JsonArray) resourceObject.get("searchParam");
					for(JsonElement searchElement: searchArray) {
						JsonObject searchObject = (JsonObject) searchElement;
						String searchParameterName = searchObject.get("name").getAsString();
						searchParameterSet.add(searchParameterName);
					}
				}
			}
		}
		return spMap;
	}
		
}