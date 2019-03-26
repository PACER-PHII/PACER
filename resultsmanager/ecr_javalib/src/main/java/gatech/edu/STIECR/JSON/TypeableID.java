package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TypeableID implements Comparable<TypeableID>{
	@JsonProperty("value")
	private String value;
	@JsonProperty("type")
	private String type;
	
	public String getvalue() {
		return value;
	}
	public void setvalue(String value) {
		this.value = value;
	}
	public String gettype() {
		return type;
	}
	public void settype(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "TypeableID [value=" + value + ", type=" + type + "]";
	}
	@Override
	public int compareTo(TypeableID o) {
		if(this.value.equals(o.getvalue())) {
			if(this.type.equals(o.gettype())) {
				return 0;
			}
			else {
				return this.type.compareTo(o.gettype());
			}
		}
		return this.value.compareTo(o.getvalue());
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		TypeableID other = (TypeableID) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
}