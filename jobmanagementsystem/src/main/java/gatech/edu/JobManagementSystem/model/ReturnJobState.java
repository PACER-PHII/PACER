package gatech.edu.JobManagementSystem.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ReturnJobState {
	private Integer jobId;
	private String jobType;
	private Map<String,String> params = new HashMap<String,String>();
	private JsonNode results;
	private String timeStarted;
	private String jobState;
	private List<String> errors;
	
	public ReturnJobState() {
		this.jobId = -1;
		this.jobType = "";
		this.timeStarted = "";
		this.jobState = "";
	}
	
	public ReturnJobState(JobState jobState) {
		this.jobId = jobState.getJobId();
		this.jobType = jobState.getJobType();
		this.params = jobState.getParams();
		this.timeStarted = jobState.getTimeStarted();
		this.jobState = jobState.getJobState();
		ObjectMapper mapper = new ObjectMapper();
		try {
			this.results = mapper.readTree(jobState.getResults());
		} catch (IOException e) {
			this.results = JsonNodeFactory.instance.objectNode();
		}
		this.errors = jobState.getErrors();
	}

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

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public JsonNode getResults() {
		return results;
	}

	public void setResults(JsonNode results) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errors == null) ? 0 : errors.hashCode());
		result = prime * result + ((jobId == null) ? 0 : jobId.hashCode());
		result = prime * result + ((jobState == null) ? 0 : jobState.hashCode());
		result = prime * result + ((jobType == null) ? 0 : jobType.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((results == null) ? 0 : results.hashCode());
		result = prime * result + ((timeStarted == null) ? 0 : timeStarted.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReturnJobState other = (ReturnJobState) obj;
		if (errors == null) {
			if (other.errors != null)
				return false;
		} else if (!errors.equals(other.errors))
			return false;
		if (jobId == null) {
			if (other.jobId != null)
				return false;
		} else if (!jobId.equals(other.jobId))
			return false;
		if (jobState == null) {
			if (other.jobState != null)
				return false;
		} else if (!jobState.equals(other.jobState))
			return false;
		if (jobType == null) {
			if (other.jobType != null)
				return false;
		} else if (!jobType.equals(other.jobType))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (results == null) {
			if (other.results != null)
				return false;
		} else if (!results.equals(other.results))
			return false;
		if (timeStarted == null) {
			if (other.timeStarted != null)
				return false;
		} else if (!timeStarted.equals(other.timeStarted))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ReturnJobState [jobId=" + jobId + ", jobType=" + jobType + ", params=" + params + ", results=" + results
				+ ", timeStarted=" + timeStarted + ", jobState=" + jobState + ", errors=" + errors + "]";
	}
}