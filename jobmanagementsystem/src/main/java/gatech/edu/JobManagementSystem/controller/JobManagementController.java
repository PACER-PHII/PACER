package gatech.edu.JobManagementSystem.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hl7.fhir.r4.model.Bundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import ca.uhn.fhir.context.FhirContext;
import gatech.edu.JobManagementSystem.model.Action;
import gatech.edu.JobManagementSystem.model.ActionType;
import gatech.edu.JobManagementSystem.model.JobState;
import gatech.edu.JobManagementSystem.model.ListType;
import gatech.edu.JobManagementSystem.model.Person;
import gatech.edu.JobManagementSystem.model.PersonList;
import gatech.edu.JobManagementSystem.model.ReturnJobState;
import gatech.edu.JobManagementSystem.model.ClarityNLPaaS.NLPaaSResult;
import gatech.edu.JobManagementSystem.model.ProcessImpl.RestAction;
import gatech.edu.JobManagementSystem.repo.ActionRepository;
import gatech.edu.JobManagementSystem.repo.JobStateRepository;
import gatech.edu.JobManagementSystem.repo.JsonTestRepository;
import gatech.edu.JobManagementSystem.repo.PersonListRepository;
import gatech.edu.JobManagementSystem.service.NLPaaSRequestService;
import gatech.edu.JobManagementSystem.service.NLPaaSToRegistryBundleService;
import gatech.edu.JobManagementSystem.service.TestJobRunnerService;
import gatech.edu.JobManagementSystem.util.JMSUtil;

@CrossOrigin
@RestController
public class JobManagementController {
	
	private static final Logger log = LoggerFactory.getLogger(JobManagementController.class);
	private PersonListRepository personListRepository;
	private ActionRepository actionRepository;
	private JsonTestRepository jsonTestRepository;
	private JobStateRepository jobStateRepository;
	private TaskScheduler taskScheduler;
	private ObjectMapper objectMapper;
	private TestJobRunnerService testJobRunnerService;
	private NLPaaSRequestService nlpaasRequestService;
	private NLPaaSToRegistryBundleService nlpaasToRegistryBundleService;
	
	@Autowired
	public JobManagementController(PersonListRepository personListRepository, ActionRepository actionRepository, 
			TaskScheduler taskScheduler, JsonTestRepository jsonTestRepository, JobStateRepository jobStateRepository, 
			TestJobRunnerService testJobRunnerService, NLPaaSRequestService nLPaaSRequestService,
			NLPaaSToRegistryBundleService nLPaaSToRegistryBundleService) {
		this.personListRepository = personListRepository;
		this.actionRepository = actionRepository;
		this.jsonTestRepository = jsonTestRepository;
		this.jobStateRepository = jobStateRepository;
		this.taskScheduler = taskScheduler;
		this.testJobRunnerService = testJobRunnerService;
		this.nlpaasRequestService = nLPaaSRequestService;
		this.nlpaasToRegistryBundleService = nLPaaSToRegistryBundleService;
		objectMapper = new ObjectMapper();
	}

	
	@RequestMapping(value = "List", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<JsonNode> postPersonList(@RequestBody PersonList list,HttpServletRequest request){
		log.debug("personlist before presistenceprep:"+list.toString());
		JMSUtil.perparePersonListForPersistence(list);
		log.debug("personlist after presistenceprep:"+list.toString());
		personListRepository.save(list);
		Action action = list.getAction();
		if(action.getActionType() == ActionType.REST) {
			action = new RestAction(action);
		}
		log.debug("action:"+action.toString());
		if(action != null) {
			if(action.getCronString() != null && list.getListType() != ListType.SINGLE_USE) {
				log.debug("scheduling action:"+action.toString());
				taskScheduler.schedule(action, new CronTrigger(action.getCronString()));
			}
			action.run();
		}
		ArrayNode results = JsonNodeFactory.instance.arrayNode();
		for(Person person:list.getListElements()) {
			if(person.getResult() != null) {
				JsonNode node = null;
				try {
					node = objectMapper.readTree(person.getResult());
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(node != null) {
					results.add(node);
				}
			}
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		try {
			responseHeaders.setLocation(new URI("/List/"+list.getName()));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ResponseEntity<JsonNode>(results,responseHeaders,HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "List/{name}", method = RequestMethod.GET)
	public ResponseEntity<PersonList> getECR(@PathVariable("name") String name){
		PersonList personList = personListRepository.findByName(name);
		return new ResponseEntity<PersonList>(personList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "Action", method = RequestMethod.POST)
	public ResponseEntity<Action> postProcess(@RequestBody Action action){
		actionRepository.save(action);
		//TODO: Use TaskManager object to schedule process
		return new ResponseEntity<Action>(action,HttpStatus.CREATED);
	}
	
	// function gets data from specified url and displays it at the given endpoint
	@RequestMapping(value = "test", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<JobState> getJobState() {
		RestTemplate restTemplate = new RestTemplate();
		String uri = "https://60914d9350c2550017677f33.mockapi.io/jobs/1";
		JobState response = restTemplate.getForObject(uri, JobState.class);
		return new ResponseEntity<JobState>(response, HttpStatus.OK);
	}
	
	// Currently working on a two parter:
	// Part 1: POST a new job to the db with jobState of running, run TestJobRunnerService and return location
	@RequestMapping(value = "Jobs", method = RequestMethod.POST)
	@Transactional
	public ResponseEntity<String> postJob(@RequestBody JobState job, HttpServletRequest request){
		job.setResults("");
		job.setErrors(new ArrayList<String>());
		String timeStarted = JMSUtil.formatDate(System.currentTimeMillis());
		job.setTimeStarted(timeStarted);
		job.setJobState("Not Started");
		jobStateRepository.save(job);
		String result = "Job Run Unsuccessful";
		HttpHeaders responseHeaders = new HttpHeaders();
		try {
			responseHeaders.setLocation(new URI("/Jobs/"+job.getJobId()));
			result = "Job results can be found at /Jobs/"+job.getJobId();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		nlpaasRequestService.makeNLPaaSRequest(job); //This is an asynchronous request
		return new ResponseEntity<String>(result, responseHeaders, HttpStatus.CREATED);
	}
	
	// Part 2: Retrieve that job state and display it at the new location
	@RequestMapping(value = "Jobs/{jobid}", method = RequestMethod.GET)
	public ResponseEntity<ReturnJobState> getJob(@PathVariable("jobid") Integer jobId){
		JobState job = jobStateRepository.findByJobId(jobId);
		if(job == null) {
			ReturnJobState returnJobState = new ReturnJobState();
			return new ResponseEntity<ReturnJobState>(returnJobState, HttpStatus.NOT_FOUND);
		}
		ReturnJobState returnJobState = new ReturnJobState(job);
		return new ResponseEntity<ReturnJobState>(returnJobState, HttpStatus.OK);
	}
	
	//Test NLPaaSServiceEndpoint
	@RequestMapping(value = "TestRegistryConversion", method = RequestMethod.POST, produces = { "application/json" })
	public ResponseEntity<String> testRegistryConversion(@RequestBody JsonNode body) throws JsonParseException, JsonMappingException, IOException{
		List<NLPaaSResult> resultList = new ArrayList<NLPaaSResult>();
		ObjectMapper objectMapper = new ObjectMapper();
		if(body.isArray()) {
			for(JsonNode resultJsonItem:(ArrayNode) body) {
				NLPaaSResult result = objectMapper.convertValue(resultJsonItem, NLPaaSResult.class);
				resultList.add(result);
			}
		}
		else {
			NLPaaSResult result = objectMapper.convertValue(body, NLPaaSResult.class);
			resultList.add(result);
		}
		Bundle returnBundle = nlpaasToRegistryBundleService.convert(resultList, "SyphilisRegistry", "https://apps.hdap.gatech.edu/omoponfhir3/fhir"); //Static jobtype and serverurl for now
		String returnString = FhirContext.forR4().newJsonParser().encodeResourceToString(returnBundle);
		return new ResponseEntity<String>(returnString, HttpStatus.OK);
	}
	
	//TODO: merge lists together.
	
	public PersonList mergeLists(PersonList listA, PersonList listB) {
		if(listA.getAction() == null) {
			listA.setAction(listB.getAction());
		}
		if(listA.getJobType() == null) {
			listA.setJobType(listB.getJobType());
		}
		if(listA.getListType() == null) {
			listA.setListType(listB.getListType());
		}
		if(listA.getRunType() == null) {
			listA.setRunType(listB.getRunType());
		}
		listA.getListElements().addAll(listB.getListElements());
		return listA;
	}
}