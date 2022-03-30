package gatech.edu.JobManagementSystem.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ca.uhn.fhir.parser.IParser;
import gatech.edu.JobManagementSystem.model.JobState;
import gatech.edu.JobManagementSystem.model.ClarityNLPaaS.NLPaaSResult;
import gatech.edu.JobManagementSystem.repo.JobStateRepository;

@Service
public class NLPaaSRequestService {
	
	private static final Logger log = LoggerFactory.getLogger(NLPaaSRequestService.class);
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private NLPaaSServiceProperties properties;
	
	@Autowired
	private NLPaaSToRegistryBundleService nLPaaSToRegistryBundleService;
	
	@Autowired
	private JobStateRepository jobStateRepository;
	
	@Autowired
	private PatientIdentifierService patientIdentifierService;
	
	@Autowired
	private IParser fhirJsonParserR4;

	@Async
	public CompletableFuture<JobState> makeNLPaaSRequest(JobState jobState){
		log.info(buildJobIdLogLine(jobState) + " Resolving patient identifier");
		String patient_id = jobState.getParams().get("patient_id");
		try {
			patient_id = patientIdentifierService.getFhirIdByIdentifier(jobState.getParams().get("patient_id"));
		} catch (Exception e) {
			log.error(buildJobIdLogLine(jobState) + "Error retreiving patient identifier...");
			e.printStackTrace();
			jobState.getErrors().add(e.getLocalizedMessage());
			jobState.setJobState("error");
			jobStateRepository.save(jobState);
			return CompletableFuture.completedFuture(jobState);
		}
		
		log.info(buildJobIdLogLine(jobState) + "Calling makeNLPaaSRequest");
		jobState.setJobState("running");
		jobStateRepository.save(jobState);
		log.info(buildJobIdLogLine(jobState) + "Retrieving NLPaaSResults");
		List<NLPaaSResult> resultList;
		try {
			resultList = retrieveNLPaaSResults(jobState, patient_id);
		} catch (Exception e) {
			log.error(buildJobIdLogLine(jobState) + "Error collecting bundle results...");
			e.printStackTrace();
			jobState.getErrors().add(e.getLocalizedMessage());
			jobState.setJobState("error");
			jobStateRepository.save(jobState);
			return CompletableFuture.completedFuture(jobState);
		}
		//Until NLPaaS is fixed, use the fhir server to get the source resources.
		log.info(buildJobIdLogLine(jobState) + "Getting the source resources for the result keys in the NLPaaSResults.");
		resultList = fixNLPaaSResultKey(resultList);
		log.info(buildJobIdLogLine(jobState) + "Converting NLPaaS results to bundle");
		Bundle returnBundle;
		try {
			returnBundle = nLPaaSToRegistryBundleService.convert(resultList, jobState.getJobType(), properties.getFhirEndpointURL());
		} catch (JsonProcessingException e) {
			log.error(buildJobIdLogLine(jobState) + "Error converting registry bundle results...");
			e.printStackTrace();
			jobState.getErrors().add(e.getLocalizedMessage());
			jobState.setJobState("error");
			jobStateRepository.save(jobState);
			return CompletableFuture.completedFuture(jobState);
		}
		String resultsString = fhirJsonParserR4.encodeResourceToString(returnBundle);
		
		log.info("Conversion of NLPaaS results to bundle complete!");
		jobState.setResults(resultsString);
		jobState.setJobState("completed");
		log.info(buildJobIdLogLine(jobState) + "Saving results to database");
		jobStateRepository.save(jobState);
		return CompletableFuture.completedFuture(jobState);
	}
	
	public List<NLPaaSResult> retrieveNLPaaSResults(JobState jobState, String patient_id) {
		log.info("Calling retrieveNLPaaSResults");
		List<NLPaaSResult> returnList = new ArrayList<NLPaaSResult>();
		List<String> serviceJobs = properties.getServiceJobs().get(jobState.getJobType()) == null ? new ArrayList<String>() : properties.getServiceJobs().get(jobState.getJobType());
		String postString = properties.customJobPostBody;
		postString = postString.replace("${patient_id}", patient_id); //Retrieve patient id from request
		for (String serviceJob:serviceJobs) {
			List<NLPaaSResult> results = runNLPaaSJob(jobState,serviceJob,postString);
			returnList.addAll(results); //Make sure this is truly asyncronous
		}
		return returnList;
	}
	
	public List<NLPaaSResult> runNLPaaSJob(JobState jobState, String jobName, String postBodyString){
		URL baseURL = null;
		URL postCustomJobUrl = null;
		/*try {
			//baseURL = new URL(properties.endpointURL);
			//postCustomJobUrl = new URL(baseURL, "job/custom/custom_"+jobName+"_v1");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String postCustomJobString = properties.endpointURL + "job/custom/custom_"+jobName;
		log.info("Running NLPaaS Job Named:"+jobName + " at endpoint:" + postCustomJobString);
		log.info("NLPaaS body:"+postBodyString);
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Content-Type", "application/json");
		httpHeaders.add("Accept", "application/json, application/*+json");
		HttpEntity<String> requestEntity = new HttpEntity<>(postBodyString, httpHeaders);
		ResponseEntity<NLPaaSResult[]> response = null;
		try {
			response = restTemplate.postForEntity(postCustomJobString, requestEntity, NLPaaSResult[].class);
		}
		catch (Exception e) {
			log.error("Error requesting job from NLPaaS");
			log.error(e.getMessage());
		}
		log.info("Response from NLPasS for Job Named:"+jobName + "," + response.toString());
		NLPaaSResult[] resultObject = response.getBody();
		List<NLPaaSResult> resultList = Arrays.asList(resultObject);
		return resultList;
	}
	
	//Temporary fix to get the result key in order
	private List<NLPaaSResult> fixNLPaaSResultKey(List<NLPaaSResult> results){
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.set("Authorization", "Basic Y2xpZW50OnNlY3JldA=="); //Just cheating my way into the results for now. 
		HttpEntity<String> entity = new HttpEntity<>("body", headers);
		for(NLPaaSResult resultObj:results) {
			if(!testResultNull(resultObj)) {
				if(resultObj.getResourceType() != null && !resultObj.getResourceType().isEmpty()
						&& resultObj.getId() != null && !resultObj.getId().isEmpty()) {
					String resourceEndpoint = properties.getFhirEndpointURL() + "/" + resultObj.getResourceType()
						+ "/" + resultObj.getId();
					String response = restTemplate.exchange(resourceEndpoint, HttpMethod.GET, entity, String.class).getBody();
					resultObj.setResult(response);
				}
			}
		}
		return results;
	}
	
	private boolean testResultNull(NLPaaSResult resultObj) {
		Object resultRaw = resultObj.getResult();
		return (resultRaw == null) ||
				(resultRaw instanceof String && ((String)resultRaw).isEmpty()) ||
				(resultRaw instanceof HashMap && ((HashMap)resultRaw).keySet().isEmpty());
	}
	
	public NLPaaSServiceProperties getProperties() {
		return properties;
	}

	public void setProperties(NLPaaSServiceProperties properties) {
		this.properties = properties;
	}
	
	private String buildJobIdLogLine(JobState jobState) {
		return "---Job Number " + jobState.getJobId().toString() + "---";
	}
}