package gatech.edu.JobManagementSystem.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

@CrossOrigin
@RestController
public class MockController {

	@Autowired
	ObjectMapper objectMapper;
	
	@GetMapping(path = "mock/med-treatment", produces = MediaType.APPLICATION_JSON_VALUE)
	public JsonNode mockMedTreatment() {
		Resource resource = new ClassPathResource("SmartPacer_Pen_and_Ceft_Mockup_Example.json");
		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(resource.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return JsonNodeFactory.instance.objectNode();
		}
		return jsonNode;
	}
}
