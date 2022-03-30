package gatech.edu.JobManagementSystem.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="job")
public class JobState{
	@Id
	@Column(name = "jobId")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer jobId;
	@Column(name = "jobType")
	private String jobType;
	@Column(name = "params")
	@ElementCollection(fetch = FetchType.EAGER)
	private Map<String,String> params = new HashMap<String,String>();
	@Column(name = "results", length = 1000000)
	private String results;
	@Column(name = "timeStarted")
	private String timeStarted;
	@Column(name = "jobState")
	private String jobState;
	@Column(name = "errors")
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> errors; 
	
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public Map<String,String> getParams() {
		return params;
	}
	public void setParams(Map<String,String> params) {
		this.params = params;
	}
	
	public String getResults() {
		return results;
	}
	public void setResults(String results) {
		this.results = results;
	}
	
	public String getTimeStarted() {
		return timeStarted;
	}
	public void setTimeStarted(String timeStarted) {
		this.timeStarted = timeStarted;
	}
	
	public String getJobState() {
		return jobState;
	}
	
	public void setJobState(String jobState) {
		this.jobState = jobState;
	}
	public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	
}
