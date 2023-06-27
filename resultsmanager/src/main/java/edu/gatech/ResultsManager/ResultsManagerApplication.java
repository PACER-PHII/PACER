package edu.gatech.ResultsManager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import ca.uhn.fhir.context.FhirContext;

@SpringBootApplication
public class ResultsManagerApplication extends SpringBootServletInitializer{

	FhirContext ctx;
	public static void main(String[] args) {
		SpringApplication.run(ResultsManagerApplication.class, args);
	}
	@Bean
	public FhirContext getCtx(){
		if(ctx == null){
			ctx = FhirContext.forR4();
		}
		return ctx;
	}
}