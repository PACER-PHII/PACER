package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabResult extends CodeableConcept{
	@JsonProperty("Date")
	private String date = "";
	@JsonProperty("Value")
	private String value = "";
	@JsonProperty("Unit")
	private CodeableConcept unit;
	
	public LabResult() {
		unit = new CodeableConcept();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public CodeableConcept getUnit() {
		return unit;
	}

	public void setUnit(CodeableConcept unit) {
		this.unit = unit;
	}
	
	public static LabResult buildLabResult(String string) {
		LabResult returnObj = new LabResult();
		String[] parts = string.split(":");
		if(parts.length > 1) {
			returnObj.setValue(parts[0]);
			String unitString = string.substring(string.indexOf(":"));
			returnObj.setUnit(CodeableConcept.buildCodeableConcept(unitString));
		}
		return returnObj;
	}
	
	@Override
	public int compareTo(CodeableConcept o) {
		if(o instanceof LabResult) {
			LabResult lro = (LabResult) o;
			if(!lro.getDate().equals(this.date)) {
				return lro.getDate().compareTo(this.date);
			}
			if(!lro.getValue().equals(this.value)) {
				return lro.getValue().compareTo(this.value);
			}
			if(!lro.getUnit().equals(this.unit)) {
				return lro.getUnit().compareTo(this.unit);
			}
		}
		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LabResult))
			return false;
		LabResult other = (LabResult) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
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

	@Override
	public String toString() {
		return "LabResult [date=" + date + ", value=" + value + ", unit=" + unit + "]";
	}
}
