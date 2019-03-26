package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OIDValue {
	@JsonProperty("Value")
	private String value = "";
	@JsonProperty("ValueSetOID")
	private String valueSetOID = "";

	public String getvalue() {
		return value;
	}

	public void setvalue(String value) {
		this.value = value;
	}

	public String getvalueSetOID() {
		return valueSetOID;
	}

	public void setvalueSetOID(String valueSetOID) {
		this.valueSetOID = valueSetOID;
	}

	@Override
	public String toString() {
		return value + ":" + valueSetOID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((valueSetOID == null) ? 0 : valueSetOID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof OIDValue))
			return false;
		OIDValue other = (OIDValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (valueSetOID == null) {
			if (other.valueSetOID != null)
				return false;
		} else if (!valueSetOID.equals(other.valueSetOID))
			return false;
		return true;
	}
}
