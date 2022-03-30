package gatech.edu.JobManagementSystem.model.ProcessImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import gatech.edu.JobManagementSystem.model.Action;
import gatech.edu.JobManagementSystem.model.ActionType;
import gatech.edu.JobManagementSystem.model.Person;
import gatech.edu.JobManagementSystem.model.PersonProcessState;
import gatech.edu.JobManagementSystem.util.JMSUtil;

public class RestAction extends Action{
	private static final Logger log = LoggerFactory.getLogger(RestAction.class);
	
	public RestAction() {
		super();
		actionType = ActionType.REST;
	}
	
	public RestAction(Action action) {
		super();
		this.actionType = action.getActionType();
		this.cronString = action.getCronString();
		this.name = action.getName();
		this.id = action.getId();
		this.params = action.getParams();
		this.personList = action.getPersonList();
	}
	
	public void run() {
		log.debug("Running RestAction:"+this.toString());
		Set<Person> list = personList.getRunnableList();
		for(Person person : list) {
			log.debug("Running RestAction for person:"+person.toString());
			boolean runBefore = false;
			switch(person.getProcessState()) {
			case NEW_COMPLETE:
			case OLD_COMPLETE:
			case ERROR:
				runBefore = true;
				break;
			}
			person.setProcessState(PersonProcessState.PROCESSING);
			RestTemplate rest = new RestTemplate();
			Map<String,String> paramsCopy = new HashMap<String,String>(params);
			String endpoint = paramsCopy.get("endpoint");
			endpoint = JMSUtil.deannotateString(personList, this, person, endpoint);
			String operation = paramsCopy.get("operation");
			String body = params.get("body");
			body = JMSUtil.deannotateString(personList, this, person, body);
			paramsCopy.remove(endpoint);
			paramsCopy.remove(operation);
			log.debug("rest endpoint:"+endpoint);
			log.debug("rest body:"+body);
			log.debug("rest operation:"+operation);
			String output = "";
			try {
				switch(operation) {
				case "GET":
					output = rest.getForEntity(endpoint, String.class, paramsCopy).getBody();
					break;
				case "POST":
					output = rest.postForEntity(endpoint, body, String.class, paramsCopy).getBody();
					break;
				case "PUT":
					rest.put(endpoint, body, paramsCopy);
					break;
				case "DELETE":
					rest.delete(endpoint, paramsCopy);
					break;
				}
			}
			catch(Exception e) {
				log.debug("exception while requesting:"+e);
				person.setProcessState(PersonProcessState.ERROR);
				continue;
			}
			log.debug("output:"+output);
			person.setResult(output);
			if(runBefore)
				person.setProcessState(PersonProcessState.OLD_COMPLETE);
			else
				person.setProcessState(PersonProcessState.NEW_COMPLETE);
		}
	}
}