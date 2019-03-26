package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CodeableConcept implements Comparable<CodeableConcept>{
	@JsonProperty("Code")
	private String code = "";
	@JsonProperty("System")
	private String system = "";
	@JsonProperty("Display")
	private String display = "";

	public CodeableConcept() {}
	
	public CodeableConcept(String system, String display, String code) {
		this.code = code;
		this.system = system;
		this.display = display;
	}
	public String getcode() {
		return code;
	}

	public void setcode(String code) {
		this.code = code;
	}

	public String getsystem() {
		return system;
	}

	public void setsystem(String system) {
		this.system = system;
	}

	public String getdisplay() {
		return display;
	}

	public void setdisplay(String display) {
		this.display = display;
	}
	
	public String toString() {
		return code + ":" + system + ":" + display;
	}
	
	public static CodeableConcept buildCodeableConcept(String string) {
		CodeableConcept returnObj = new CodeableConcept();
		String[] parts = string.split(":");
		if(parts.length > 1) {
			returnObj.setcode(parts[0]);
			returnObj.setsystem(parts[1]);
			returnObj.setdisplay(parts[2]);
		}
		return returnObj;
	}
	/**
	 * 
	 * @return code/system format
	 */
	@JsonIgnore
	public String getSourceValue() {
		return code + "/" + system;
	}

	@Override
	public int compareTo(CodeableConcept o) {
		if(this.code.equals(o.code)) {
			if(this.system.equals(o.system))
				return 0;
		}
		else if(this.system.equals(o.system)) {
			return this.code.compareTo(o.code);
		}
		return this.system.compareTo(o.system);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof CodeableConcept))
			return false;
		CodeableConcept other = (CodeableConcept) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		return true;
	}
	
}
