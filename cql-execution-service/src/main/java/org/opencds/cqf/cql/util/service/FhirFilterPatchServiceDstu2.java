package org.opencds.cqf.cql.util.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hl7.fhir.dstu3.hapi.ctx.FhirDstu3;
import org.hl7.fhir.r4.hapi.ctx.FhirR4;
import org.opencds.cqf.cql.util.entity.FhirFilterPatch.ParameterEntity;
import org.opencds.cqf.cql.util.entity.FhirFilterPatch.ParameterMapEntity;
import org.opencds.cqf.cql.util.entity.FhirFilterPatch.SearchParameterQueryEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.TypeRef;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.FhirDstu2;
import ca.uhn.fhir.model.primitive.CodeDt;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;

public class FhirFilterPatchServiceDstu2 {
	Logger log = Logger.getLogger(FhirFilterPatchServiceDstu2.class);
	
	Map<String, Map<String, ParameterEntity> > baseParameterMapEntity;
	FhirContext fhirContext;
	IParser parser;
	Gson gson;
	ObjectMapper objectMapper;
	Configuration config;
	public FhirFilterPatchServiceDstu2(FhirContext fhirContext) {
		String filePath = null;
		if(fhirContext.getVersion() instanceof FhirDstu2) {
			filePath = "search-parameter-map-dstu2.json";
		}
		else if(fhirContext.getVersion() instanceof FhirDstu3) {
			filePath = "search-parameter-map-stu3.json";
		}
		else if(fhirContext.getVersion() instanceof FhirR4) {
			filePath = "search-parameter-map-r4.json";
		}
		Reader reader = null;
		reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(filePath));
		gson = new Gson();
		objectMapper = new ObjectMapper();
		config = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider())
                .build();
		try {
			reader = new BufferedReader(new FileReader(new File("search-parameter-map-dstu2.json")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Type twoLevelMapType = new TypeToken<HashMap<String, HashMap<String, ParameterEntity> > >() {}.getType();
		baseParameterMapEntity = gson.fromJson(reader, twoLevelMapType);
		this.fhirContext = fhirContext;
		parser = fhirContext.newJsonParser();
	}
	/**
	 * Transform the result set to a FILTERED result
	 * @param searchParameterNeeded A map of expressions to search Parameters needed
	 * @param gapMap A map describing the gap between what the expression needed vs what was used
	 * @param queryString queryString actuallyUsed in definition
	 * @param fhirResultsString fhir data as string
	 * @return
	 */
	public String filterResults(Map<String, Object> searchParameterNeeded, Map<String, Set<String> > gapMap, String queryString, String fhirResultsString) {
		log.debug("--- Initializing Filter Service for query:"+queryString+" ---");
		//Decode URL values to have clean comparisons
		try {
			queryString = URLDecoder.decode(queryString, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		//Get original expression's search parameters used
		Set<SearchParameterQueryEntity> searchParametersRequested = createSearchParameterQueryEntitySet(queryString);
		Set<String> searchParameterKeysUsed = new HashSet<String>();
		for(SearchParameterQueryEntity spQueryEntity:searchParametersRequested) {
			searchParameterKeysUsed.add(spQueryEntity.getKey());
		}
		//Check if there was a gap with the server
		String searchResourceName = (String) searchParameterNeeded.get("resourceName");
		Set<String> resourceSPGap = gapMap.get(searchResourceName);
		//See if a gap actually occured
		Set<String> searchParametersRequestedNoModifier = new HashSet<String>(searchParameterKeysUsed);
		for(String s: searchParametersRequestedNoModifier) {
			if(s.indexOf(":") != -1) {
				searchParametersRequestedNoModifier.remove(s);
				searchParametersRequestedNoModifier.add(s.substring(s.indexOf(':') + 1));
			}
		}
		Set<String> realizedGap = new HashSet<String>(searchParametersRequestedNoModifier);
		realizedGap.retainAll(resourceSPGap);
		if(realizedGap.isEmpty() || fhirResultsString.equalsIgnoreCase("[]")) {
			//No gap to cover!
			return fhirResultsString;
		}
		log.debug("--- Gap in request is:"+realizedGap+" ---");
		//Get the resources BACK into fhir java form
		JsonNode resultsNode = null;
		try {
			resultsNode = objectMapper.readTree(fhirResultsString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<IResource> fhirResults = new ArrayList<IResource>();
		for(JsonNode resourceNode:resultsNode) {
			IResource resource = null;
			try {
				resource = (IResource)parser.parseResource(objectMapper.writeValueAsString(resourceNode));
			} catch (ConfigurationException | DataFormatException | JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fhirResults.add(resource);
		}
		IResource firstResource = fhirResults.get(0); //Pull first resource to check resourceType
		
		Map<String, ParameterEntity> parameterSet = baseParameterMapEntity.get(firstResource.getResourceName().toString());
		for(String name:parameterSet.keySet()) {
			ParameterEntity parameterEntity = parameterSet.get(name);
			SearchParameterQueryEntity searchQueryEntity = getSearchParameterQueryByKey(searchParametersRequested,name);
			if(realizedGap.contains(parameterEntity.getName())) {
				log.debug("--- Applying parameter :"+parameterEntity.getName()+" ---");
				//Handle Token SP Type
				if(parameterEntity.getType().equalsIgnoreCase("token")) {
					fhirResults = handleTokenParameterGap(fhirResults, searchQueryEntity, parameterEntity.getPath());
				}
			}
		}
		//Back to a list of strings for the purpose of return
		List<String> stringList = new ArrayList<String>();
		for(IResource r:fhirResults) {
			String resourceString = parser.encodeResourceToString(r);
			stringList.add(resourceString);
		}
		String returnString = stringList.toString();
		return returnString;
	}
	
	private List<IResource> handleTokenParameterGap(List<IResource> fhirResults, SearchParameterQueryEntity searchQueryEntity, String fhirPath) {
		List<IResource> returnResults = new ArrayList<IResource>();
		log.debug("--- Applying token parameter:"+searchQueryEntity.getKey()+" ---");
		for(IResource resource: fhirResults) {
			IResource addedResource = handleTokenParameterGap(resource,searchQueryEntity,fhirPath);
			if(addedResource != null) {
				returnResults.add(addedResource);
			}
		}
		return returnResults;
	}
	
	private IResource handleTokenParameterGap(IResource resource, SearchParameterQueryEntity searchQueryEntity, String fhirPath) {
		String resourceString = parser.encodeResourceToString(resource);
		ReadContext resourceJavaPath = JsonPath.using(config).parse(resourceString);
		//Try coding datatype handler
		log.debug("--- Trying coding handler ---");
		try {
			ArrayNode codingsNode = resourceJavaPath.read("$." + fhirPath + ".coding[*]",ArrayNode.class);
			log.debug("--Codings as string:"+ codingsNode.toString()+" ---");
			boolean anyMatch = false;
			for(JsonNode coding: codingsNode) {
				if(compareCodingToSearchParameterEntity(coding, searchQueryEntity)) {
					anyMatch = true;
					break;
				}
			}
			if(anyMatch) {
				return resource;
			}
			else {
				log.debug("--- NO MATCH Removing Resource id: "+ resource.getId()+" ---");
				return null;
			}
		}
		catch(NullPointerException e) {
		}
		log.debug("--- Trying code handler ---");
		//Try code datatype handler
		try {
			String code = resourceJavaPath.read("$." + fhirPath);
			
			if(compareCodeToSearchParameterEntity(code, searchQueryEntity)) {
				return resource;
			}
			else {
				log.debug("--- NO MATCH Removing Resource id: "+ resource.getId()+" ---");
				return null;
			}
		}
		catch(NullPointerException e) {
		}
		return null;
	}
	
	//Data Type Comparators
	private boolean compareCodingToSearchParameterEntity(JsonNode coding, SearchParameterQueryEntity searchQueryEntity) {
		if((searchQueryEntity.getModifier() != null && searchQueryEntity.getModifier().equalsIgnoreCase("in")) ||
				searchQueryEntity.getValue().contains(",")) {
			String[] values = searchQueryEntity.getValue().split(",");
			for(String value:values) {
				if(compareCodingToSmallString(coding,value)) {
					return true;
				}
			}
			return false;
		}
		else {
			return compareCodingToSmallString(coding,searchQueryEntity.getValue());
		}
	}
	
	private boolean compareCodingToSmallString(JsonNode coding, String value) {
		String[] systemAndCode = value.split("\\|");
		String system = "";
		String code = "";
		if(systemAndCode.length == 2) {
			system = systemAndCode[0];
			code = systemAndCode[1];
			log.debug("---Comparing to Search Parameter System: "+system+" Code:"+code+" ---");
			return coding.get("system").asText().equalsIgnoreCase(system) &&
					coding.get("code").asText().equalsIgnoreCase(code); 
		}
		else {
			return coding.get("code").asText().equalsIgnoreCase(code);
		}
	}
	
	private boolean compareCodeToSearchParameterEntity(String code, SearchParameterQueryEntity searchQueryEntity) {
		if((searchQueryEntity.getModifier() != null && searchQueryEntity.getModifier().equalsIgnoreCase("in")) ||
				searchQueryEntity.getValue().contains(",")) {
			String[] values = searchQueryEntity.getValue().split(",");
			for(String value:values) {
				if(compareCodeToSmallString(code,value)) {
					return true;
				}
			}
			return false;
		}
		else {
			return compareCodeToSmallString(code,searchQueryEntity.getValue());
		}
	}
	
	private boolean compareCodeToSmallString(String codeDt, String value) {
		String[] systemAndCode = value.split("\\|");
		String system = "";
		String code = "";
		if(systemAndCode.length == 2) {
			system = systemAndCode[0];
			code = systemAndCode[1];
			log.debug("--- CodeType System: "+system+" CodeType Code:"+code+" ---");
			return codeDt.equalsIgnoreCase(code); 
		}
		else {
			log.debug(" CodeType Code:"+code+" ---");
			return codeDt.equalsIgnoreCase(value);
		}
	}
	
	/**
	 * Turns a list of &'d params with possible modifiers (:) into a Set of objects that describe the parameters
	 * @param queryString
	 * @return
	 */
	private Set<SearchParameterQueryEntity> createSearchParameterQueryEntitySet(String queryString){
		if(queryString.indexOf('?') != -1) {
			queryString = queryString.substring(queryString.indexOf('?') + 1);
		}
		Set<SearchParameterQueryEntity> returnList = new HashSet<SearchParameterQueryEntity>();
		String[] params = queryString.split("&");
		for(String param:params) {
			SearchParameterQueryEntity entity = new SearchParameterQueryEntity();
			String[] keyAndValue = param.split("=");
			String key = keyAndValue[0];
			String value = keyAndValue[1];
			if(key.contains(":")) {
				String[] keyAndModifier = key.split(":");
				entity.setKey(keyAndModifier[0]);
				entity.setModifier(keyAndModifier[1]);
			}
			else {
				entity.setKey(key);
			}
			entity.setValue(value);
			returnList.add(entity);
		}
		return returnList;
	}
	
	private SearchParameterQueryEntity getSearchParameterQueryByKey(Set<SearchParameterQueryEntity> set, String key) {
		for(SearchParameterQueryEntity entity:set) {
			if(entity.getKey().equalsIgnoreCase(key)) {
				return entity;
			}
		}
		return null;
	}
}