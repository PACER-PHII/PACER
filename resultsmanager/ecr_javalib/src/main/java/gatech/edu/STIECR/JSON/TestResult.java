package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TestResult implements Comparable<TestResult>{
	@JsonProperty("Value")
	private String value = "";
	@JsonProperty("ResultStatus")
	private String resultStatus = "";

	public String getvalue() {
		return value;
	}

	public void setvalue(String value) {
		this.value = value;
	}

	public String getresultStatus() {
		return resultStatus;
	}

	public void setresultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	@Override
	public String toString() {
		return value + ":" + resultStatus;
	}
	
	public static TestResult buildTestResult(String string) {
		TestResult returnObj = new TestResult();
		String[] parts = string.split(":");
		if(parts.length > 1) {
			returnObj.setvalue(parts[0]);
			returnObj.setresultStatus(parts[1]);
		}
		return returnObj;
	}

	@Override
	public int compareTo(TestResult o) {
		if(!o.getvalue().equals(this.value)) {
			return o.getvalue().compareTo(this.value);
		}
		if(!o.getresultStatus().equals(this.resultStatus)) {
			return o.getresultStatus().compareTo(this.resultStatus);
		}
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((resultStatus == null) ? 0 : resultStatus.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof TestResult))
			return false;
		TestResult other = (TestResult) obj;
		if (resultStatus == null) {
			if (other.resultStatus != null)
				return false;
		} else if (!resultStatus.equals(other.resultStatus))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
