package gatech.edu.JobManagementSystem.service;

import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import gatech.edu.JobManagementSystem.model.JobState;
import gatech.edu.JobManagementSystem.repo.JobStateRepository;

@Service
public class TestJobRunnerService {

  private static final Logger logger = LoggerFactory.getLogger(TestJobRunnerService.class);
  private JobStateRepository jobStateRepository;
  
  public TestJobRunnerService(JobStateRepository jobStateRepository) {
	  this.jobStateRepository = jobStateRepository;
  }
  
  @Async
  public CompletableFuture<JobState> runJob(JobState job) throws InterruptedException {
    logger.info("Running Job " + job.getJobId());
    job.setJobState("Running");
    jobStateRepository.save(job);
    
    // Artificial delay of 5s for demonstration purposes
    Thread.sleep(10000L);
    RestTemplate restTemplate = new RestTemplate();
	String uri = "https://jsonplaceholder.typicode.com/todos/1";
	String response = restTemplate.getForObject(uri, String.class);
    job.setJobState("Completed");
    job.setResults(response);
    jobStateRepository.save(job);
    return CompletableFuture.completedFuture(job);
  }

}