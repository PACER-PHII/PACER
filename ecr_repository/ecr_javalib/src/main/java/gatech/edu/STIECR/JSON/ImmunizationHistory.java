package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ImmunizationHistory implements Comparable<ImmunizationHistory>{
	@JsonProperty("Code")
	private String code = "";
	@JsonProperty("System")
	private String system = "";
	@JsonProperty("Date")
	private String date = "";

	private static final String[] codeSplits = {"/", " on "};

	public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}


	public String getSystem() {
		return system;
	}


	public void setSystem(String system) {
		this.system = system;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}
	
	@Override
	public String toString() {
		return code + codeSplits[0] + system + codeSplits[1] + date;
	}
	
	public static ImmunizationHistory buildImmunizationHistory(String string) {
		ImmunizationHistory returnObj = new ImmunizationHistory();
		returnObj.setCode(string.split(codeSplits[0])[0]);
		returnObj.setSystem(string.split(codeSplits[1])[0].split(codeSplits[0])[1]);
		returnObj.setDate(string.split(codeSplits[1])[1]);
		return returnObj;
	}


	@Override
	public int compareTo(ImmunizationHistory o) {
		if(this.code.equals(o.getCode()) && this.system.equals(o.getSystem()) && this.date.equals(o.getDate()))
			return 0;
		else if(this.code.equals(o.getCode()) && this.system.equals(o.getSystem())) {
			return this.date.compareTo(o.getDate());
		}
		else if(this.code.equals(o.getCode())) {
			return this.system.compareTo(o.getSystem());
		}
		return this.code.compareTo(o.getCode());
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ImmunizationHistory))
			return false;
		ImmunizationHistory other = (ImmunizationHistory) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		return true;
	}
}
