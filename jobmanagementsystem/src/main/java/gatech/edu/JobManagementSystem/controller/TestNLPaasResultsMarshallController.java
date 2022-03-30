package gatech.edu.JobManagementSystem.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import gatech.edu.JobManagementSystem.model.ClarityNLPaaS.NLPaaSResult;

@CrossOrigin
@RestController
public class TestNLPaasResultsMarshallController {

	@Autowired
	ObjectMapper objectMapper;
	
	@PostMapping(path = "test/marshallNLPaaSResult", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode mockMedTreatment(@RequestBody String nlpaasJson) {
		List<NLPaaSResult> finalResults = null;
		try {
			finalResults = objectMapper.readValue(nlpaasJson, objectMapper.getTypeFactory().constructCollectionType(List.class, NLPaaSResult.class));
		}
		catch(JsonParseException | JsonMappingException e) {
			ObjectNode errorNode = JsonNodeFactory.instance.objectNode();
			errorNode.put("ErrorMessage", e.getMessage());
			errorNode.put("ErrorLocalizedMessage", e.getLocalizedMessage());
			errorNode.put("Location", e.getLocation().toString());
			return errorNode;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JsonNode returnNode = objectMapper.convertValue(finalResults, JsonNode.class);
		return returnNode;
	}
}