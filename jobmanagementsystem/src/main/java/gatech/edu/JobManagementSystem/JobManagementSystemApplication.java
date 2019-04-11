package gatech.edu.JobManagementSystem;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAutoConfiguration
@EntityScan("gatech.edu.JobManagementSystem.model")
@EnableJpaRepositories("gatech.edu.JobManagementSystem.repo")
@ComponentScan("gatech.edu.JobManagementSystem.controller")
@ComponentScan("gatech.edu.JobManagementSystem")
@EnableScheduling
@SpringBootApplication
public class JobManagementSystemApplication extends SpringBootServletInitializer{

	private static final Logger log = LoggerFactory.getLogger(JobManagementSystemApplication.class);

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(JobManagementSystemApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(JobManagementSystemApplication.class);
	}
	
}