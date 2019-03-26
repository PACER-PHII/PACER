package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Medication implements Comparable<Medication>{
	@JsonProperty("Code")
	private String code = "";
	@JsonProperty("System")
	private String system = "";
	@JsonProperty("Display")
	private String display = "";
	@JsonProperty("Dosage")
	private Dosage dosage = new Dosage();
	@JsonProperty("Date")
	private String date = "";
	@JsonProperty("Frequency")
	private String frequency = "";
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
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public Dosage getDosage() {
		return dosage;
	}
	public void setDosage(Dosage dosage) {
		this.dosage = dosage;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getFrequency() {
		return frequency;
	}
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	public String toString() {
		return code + ":" + system + ":" + display + ":" + dosage + ":" + date + ":" + frequency;
	}
	
	public static Medication buildMedication(String string) {
		Medication returnObj = new Medication();
		String[] parts = string.split(":");
		if(parts.length > 1) {
			returnObj.setCode(parts[0]);
			returnObj.setSystem(parts[1]);
			returnObj.setDisplay(parts[2]);
			returnObj.setDosage(Dosage.buildDosage(parts[3]));
			returnObj.setDate(parts[4]);
			returnObj.setFrequency(parts[5]);
		}
		return returnObj;
	}
	
	@Override
	public int compareTo(Medication o) {
		if(!this.code.equals(o.getCode()))
			return this.code.compareTo(o.getCode());
		if(!this.system.equals(o.getSystem()))
			return this.system.compareTo(o.getSystem());
		if(!this.date.equals(o.getDate()))
			return this.date.compareTo(o.getDate());
		if(!this.display.equals(o.getDisplay()))
			return this.display.compareTo(o.getDisplay());
		if(!this.frequency.equals(o.getFrequency()))
			return this.frequency.compareTo(o.getFrequency());
		if(this.dosage != null & o.getDosage() != null) 
			return this.dosage.compareTo(o.getDosage());
		return 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((display == null) ? 0 : display.hashCode());
		result = prime * result + ((dosage == null) ? 0 : dosage.hashCode());
		result = prime * result + ((frequency == null) ? 0 : frequency.hashCode());
		result = prime * result + ((system == null) ? 0 : system.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Medication))
			return false;
		Medication other = (Medication) obj;
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
		if (display == null) {
			if (other.display != null)
				return false;
		} else if (!display.equals(other.display))
			return false;
		if (dosage == null) {
			if (other.dosage != null)
				return false;
		} else if (!dosage.equals(other.dosage))
			return false;
		if (frequency == null) {
			if (other.frequency != null)
				return false;
		} else if (!frequency.equals(other.frequency))
			return false;
		if (system == null) {
			if (other.system != null)
				return false;
		} else if (!system.equals(other.system))
			return false;
		return true;
	}
}
