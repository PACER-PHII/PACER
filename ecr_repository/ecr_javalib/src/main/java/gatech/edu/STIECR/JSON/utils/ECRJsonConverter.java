package gatech.edu.STIECR.JSON.utils;

import java.io.IOException;

import javax.persistence.AttributeConverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gatech.edu.STIECR.JSON.ECR;

public class ECRJsonConverter implements AttributeConverter<ECR, String>{

	private ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public String convertToDatabaseColumn(ECR attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Could not convert ECR to Json", e);
		}
	}

	@Override
	public ECR convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, ECR.class);
		} catch (IOException e) {
			throw new RuntimeException("Could not convert Json to ECR", e);
		}
	}

}
