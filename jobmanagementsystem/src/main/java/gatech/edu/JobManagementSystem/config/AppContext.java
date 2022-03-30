package gatech.edu.JobManagementSystem.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import gatech.edu.JobManagementSystem.async.JMSAsyncUncaughtExceptionHandler;

@Configuration
@EnableAsync
public class AppContext extends WebMvcConfigurationSupport {
	
	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}
	
	@Bean
	public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
		return new JMSAsyncUncaughtExceptionHandler();
	}
	
}