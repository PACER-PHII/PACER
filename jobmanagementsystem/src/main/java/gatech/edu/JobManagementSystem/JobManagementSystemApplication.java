package gatech.edu.JobManagementSystem;

import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

@SpringBootApplication(exclude={UserDetailsServiceAutoConfiguration.class})
@EntityScan("gatech.edu.JobManagementSystem.model")
@EnableJpaRepositories("gatech.edu.JobManagementSystem.repo")
@ComponentScan("gatech.edu.JobManagementSystem.controller")
@ComponentScan("gatech.edu.JobManagementSystem.security")
@ComponentScan("gatech.edu.JobManagementSystem.service")
@ComponentScan("gatech.edu.JobManagementSystem")
public class JobManagementSystemApplication extends SpringBootServletInitializer{

	private static final Logger log = LoggerFactory.getLogger(JobManagementSystemApplication.class);

	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JobManagementSystemApplication.class);
	}
	public static void main(String[] args) {
		new SpringApplicationBuilder(JobManagementSystemApplication.class)
        .web(WebApplicationType.SERVLET)
        .run(args);
	}

	@Bean
	public Executor taskExecutor() {
	    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	    executor.setCorePoolSize(2);
	    executor.setMaxPoolSize(2);
	    executor.setQueueCapacity(500);
	    executor.setThreadNamePrefix("JMSJob-");
	    executor.initialize();
	    return executor;
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Bean
	public FhirContext ctxStu3() {
		return FhirContext.forDstu3();
	}
	
	@Bean
	public FhirContext ctxR4() {
		return FhirContext.forR4();
	}
	
	@Bean
	public IParser fhirJsonParserStu3() {
		return ctxStu3().newJsonParser();
	}
	
	@Bean
	public IParser fhirJsonParserR4() {
		return ctxR4().newJsonParser();
	}
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
}