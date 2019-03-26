package gatech.edu.ECR_Repository;

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

@EnableAutoConfiguration
@EntityScan("gatech.edu.STIECR.DB.model")
@EnableJpaRepositories("gatech.edu.STIECR.DB.repo")
@ComponentScan("gatech.edu.ECR_Repository.controller")
@ComponentScan("gatech.edu.ECR_Repository")
@ComponentScan("gatech.edu.ECR_Repository.service")
@SpringBootApplication
public class ApplicationTest extends SpringBootServletInitializer{

	private static final Logger log = LoggerFactory.getLogger(ApplicationTest.class);

	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ApplicationTest.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(ApplicationTest.class);
	}
	
}