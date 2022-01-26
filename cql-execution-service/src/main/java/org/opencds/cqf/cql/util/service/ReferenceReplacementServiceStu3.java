package org.opencds.cqf.cql.util.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.hl7.fhir.dstu3.hapi.ctx.FhirDstu3;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.r4.hapi.ctx.FhirR4;
import org.opencds.cqf.cql.util.entity.FhirFilterPatch.ParameterEntity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import ca.uhn.fhir.context.ConfigurationException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.FhirDstu2;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class ReferenceReplacementServiceStu3 {
	Logger log = Logger.getLogger(ReferenceReplacementServiceStu3.class);
	
	FhirContext fhirContext;
	IGenericClient client;
	ObjectMapper objectMapper;
	IParser parser;
	
	public ReferenceReplacementServiceStu3(FhirContext fhirContext, IGenericClient iGenericClient) {
		this.fhirContext = fhirContext;
		this.parser = fhirContext.newJsonParser();
		this.client = iGenericClient;
		this.objectMapper = new ObjectMapper();
	}
	
	public String replaceReferenceWithCode(String fhirResultsString) {
		//Convert results back into fhir
		List<Resource> fhirResults = new ArrayList<Resource>();
		JsonNode resultsNode = null;
		try {
			resultsNode = objectMapper.readTree(fhirResultsString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(JsonNode resourceNode:resultsNode) {
			Resource resource = null;
			try {
				resource = (Resource)parser.parseResource(objectMapper.writeValueAsString(resourceNode));
			} catch (ConfigurationException | DataFormatException | JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			fhirResults.add(resource);
		}
		if(fhirResults.size() == 0) {
			return fhirResultsString;
		}
		Resource firstResource = fhirResults.get(0);
		//If MedicationStatement, check the medicationReference
		if(firstResource.getResourceType().equals(ResourceType.MedicationStatement)) {
			for(Resource resource:fhirResults) {
				MedicationStatement medicationStatement = (MedicationStatement)resource;
				//Replace medicationReference with a code by reading the medication
				if(medicationStatement.getMedicationReference() != null) {
					Reference medicationRef = medicationStatement.getMedicationReference();
					Medication medication = (Medication) client.read()
					.resource("Medication")
					.withId(medicationRef.getReference().substring(medicationRef.getReference().indexOf("/") + 1))
					.execute();
					if(medication == null) {
						log.debug("Retrieved medication is null, not modifying");
					}
					else {
						medicationStatement.setMedication(medication.getCode());
					}
				}
			}
		}
		//Back to a list of strings for the purpose of return
		List<String> stringList = new ArrayList<String>();
		for(Resource r:fhirResults) {
			String resourceString = parser.encodeResourceToString(r);
			stringList.add(resourceString);
		}
		String returnString = stringList.toString();
		return returnString;
	}
}
