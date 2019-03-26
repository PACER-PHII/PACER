package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Dosage implements Comparable<Dosage>{
	@JsonProperty("Value")
	private String value = "";
	@JsonProperty("Unit")
	private String unit = "";
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	public String toString() {
		return value + ":" + unit;
	}
	
	@JsonIgnore
	public boolean isEmpty() {
		return value.isEmpty() && unit.isEmpty();
	}
	
	public static Dosage buildDosage(String string) {
		Dosage returnObj = new Dosage();
		String[] parts = string.split(":");
		if(parts.length > 1) {
			returnObj.setValue(parts[0]);
			returnObj.setValue(parts[1]);
		}
		return returnObj;
	}
	
	@Override
	public int compareTo(Dosage o) {
		if(!this.value.equals(o.getValue()))
			return this.value.compareTo(o.getValue());
		if(!this.unit.equals(o.getUnit()))
			return this.unit.compareTo(o.getUnit());
		return 0;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Dosage))
			return false;
		Dosage other = (Dosage) obj;
		if (unit == null) {
			if (other.unit != null)
				return false;
		} else if (!unit.equals(other.unit))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}
