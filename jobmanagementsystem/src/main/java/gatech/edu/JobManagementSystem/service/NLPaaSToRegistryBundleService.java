package gatech.edu.JobManagementSystem.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;
import org.hl7.fhir.r4.model.Annotation;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Observation.ObservationStatus;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.parser.IParser;
import gatech.edu.JobManagementSystem.model.ClarityNLPaaS.NLPaaSResult;
import gatech.edu.JobManagementSystem.model.ClarityNLPaaS.NLPaaSTuple;
import gatech.edu.JobManagementSystem.util.NLPaaSToRegistryBundleUtil;

@Service
public class NLPaaSToRegistryBundleService {
	
	@Autowired
	NLPaaSServiceProperties properties;
	@Autowired
	private IParser fhirJsonParserR4;
	private ObjectMapper objectMapper;
	
	Logger logger = LoggerFactory.getLogger(NLPaaSToRegistryBundleService.class);
	
	public NLPaaSToRegistryBundleService() {
		objectMapper = new ObjectMapper();
	}
	
	public Bundle convert(List<NLPaaSResult> results, String jobType, String serviceUrl) throws JsonProcessingException {
		Bundle returnBundle = new Bundle();
		initResource(returnBundle);
		//First pass for source resources
		List<String> resultKeys = properties.getServiceJobs().get(jobType);
		for (String resultKey : resultKeys) {
			String nlpFeatureName = resultKey.substring(0, resultKey.lastIndexOf("_")); //Remove the "_v2" version number from the job name to get nlpql feature name
			List<NLPaaSResult> allResults = getResultNamed(results, nlpFeatureName);
			Reference subjectReference = null;
			//Retrieve source resources per bundle
			for(NLPaaSResult currentResult : allResults) {
				logger.debug("Converting first pass:" + currentResult.getCql_feature());
				String resultType = currentResult.getResultType();
				if(resultType == null) {
					resultType = currentResult.getResourceType();
				}
				if(NLPaaSToRegistryBundleUtil.fhirResultTypes.contains(resultType)) {
					String result = "";
					if(currentResult.getResult() instanceof String) {
						result = (String)currentResult.getResult();
					}
					else if(currentResult.getResult() instanceof HashMap) {
						result = objectMapper.writeValueAsString(currentResult.getResult());
					}
					Resource resource = parseResourceFromResult(result);
					//Add identifier (unique to this server) if none exists
					List<Identifier> identifiers = null;
					if (resource instanceof Patient) { //Get identifiers for the resources that support it
						identifiers = ((Patient) resource).getIdentifier();
						subjectReference = new Reference(((Patient) resource).getId());
					}
					else if (resource instanceof Condition) {
						Condition condition = (Condition) resource;
						identifiers = ((Condition) resource).getIdentifier();
					}
					else if (resource instanceof Observation) {
						identifiers = ((Observation) resource).getIdentifier();
					}
					else if (resource instanceof MedicationStatement) {
						identifiers = ((MedicationStatement) resource).getIdentifier();
					}
		
					if(identifiers != null && identifiers.isEmpty()) {
						Identifier customPacerIdentifier = new Identifier().setSystem(serviceUrl).setValue(resource.getId());
						identifiers.add(customPacerIdentifier);
					}
					BundleEntryComponent bec = new BundleEntryComponent();
					bec.setResource(resource);
					bec.setFullUrl(resource.getId());
					returnBundle.addEntry(bec);
				}
			}
			//Retrieve derived resources through tuple parsing
			for (NLPaaSResult currentResult : allResults) {
				logger.debug("Converting second pass:" + currentResult.getCql_feature());
				String resultType = currentResult.getResultType();
				if(resultType == null) {
					resultType = currentResult.getResourceType();
				}
				if(NLPaaSToRegistryBundleUtil.listTupleResultTypes.contains(resultType)) {
					if(!currentResult.getTuples().isEmpty()) {
						for(NLPaaSTuple tuple : currentResult.getTuples()) {
							Observation derivedObservation = new Observation();
							initResource(derivedObservation);
							CodeableConcept code = new CodeableConcept().addCoding(
									new Coding("urn:gtri:heat:syphilisregistry",tuple.getDictionary().get("questionConcept"), ""));
							derivedObservation.setCode(code);
							derivedObservation.addCategory(new CodeableConcept().addCoding(new Coding("http://terminology.hl7.org/CodeSystem/observation-category","survey","Survey")));
							String valueType = tuple.getDictionary().get("valueType");
							String answerValue = tuple.getDictionary().get("answerValue");
							if(answerValue == null || answerValue.isEmpty()) {
								continue;
							}
							if(valueType.equalsIgnoreCase("String")) {
								derivedObservation.setValue(new StringType(tuple.getDictionary().get("answerValue")));
							}
							else if(valueType.equalsIgnoreCase("Quantity")) {
								//TODO: Fix cql answervalue for quantity
								derivedObservation.setValue(parseQuantityFromString(tuple.getDictionary().get("sourceNote")));
							}
							derivedObservation.addNote(new Annotation().setText(tuple.getDictionary().get("sourceNote")));
							//Link via focus reference to source resources
							Resource sourceResource = getSourceResourceFromBundle(returnBundle, tuple.getDictionary().get("fhirResourceId"));
							if(sourceResource != null) {
								Reference reference = new Reference(sourceResource);
								derivedObservation.addFocus(reference);
							}
							if(subjectReference != null) {
								derivedObservation.setSubject(subjectReference);
							}
							BundleEntryComponent bec = new BundleEntryComponent();
							bec.setResource(derivedObservation);
							bec.setFullUrl(derivedObservation.getId());
							returnBundle.addEntry(bec);
						}
					}
				}
			}
		}
		return returnBundle;
	}
	
	public List<NLPaaSResult> getResultNamed(List<NLPaaSResult> results, String name) {
		return results.stream().filter(r -> r.getNlpql_feature().equalsIgnoreCase(name)).collect(Collectors.toList());
	}
	
	public CodeableConcept convertCQLAnswerValueToCodeableConcept(String answerValue) {
		String[] components = answerValue.split("\\^");
		String system = components[0];
		String code = components[1];
		String display = components[2];
		CodeableConcept returnCode = new CodeableConcept()
				.addCoding(new Coding(system,code,display));
		returnCode.setText(answerValue);
		return returnCode;
	}
	
	private void initResource(Resource resource) {
		resource.setId(UUID.randomUUID().toString());
	}
	
	public Map<String, String> parseTuple(String tupleString){
		Map<String, String> returnMap = new HashMap<String, String>();
		Pattern curlyBraceInner = Pattern.compile("\\{(.*?)\\}");
		Pattern arrowPattern = Pattern.compile("(.*)\\s->\\s(.*)");
		Matcher curlyBraceInnerMatcher = curlyBraceInner.matcher(tupleString);
		String tupleInner = curlyBraceInnerMatcher.group();
		String[] keyValuePairs = tupleInner.split("\\n");
		for(String keyValuePair: keyValuePairs) {
			keyValuePair.replaceAll("\\t", "");
			Matcher arrowMatcher = arrowPattern.matcher(keyValuePair);
			String key = arrowMatcher.group(1);
			String value = arrowMatcher.group(2);
			returnMap.put(key, value);
		}
		return returnMap;
	}
	
	public Resource parseResourceFromResult(String resourceString) {
		return (Resource)fhirJsonParserR4.parseResource(resourceString);
	}
	
	public Quantity parseQuantityFromString(String quantityString) {
		Pattern quantityPattern = Pattern.compile("\\s*(\\d*\\.?\\d*)\\s*\\'(\\w*)\\'");
		Matcher matcher = quantityPattern.matcher(quantityString);
		matcher.find();
		
		Quantity returnQuantity = new Quantity();
		returnQuantity.setValue(Double.parseDouble(matcher.group(1)));
		returnQuantity.setUnit(matcher.group(2));
		return returnQuantity;
	}
	
	public Resource getSourceResourceFromBundle(Bundle bundle, String id) {
		String idNumberPart = id.substring(id.lastIndexOf("/") + 1);
		for(BundleEntryComponent bec: bundle.getEntry()) {
			Resource resource = bec.getResource();
			if(resource.getId().contains(idNumberPart)) {
				return resource;
			}
		}
		return null;
	}
}