package org.opencds.cqf.cql.util.entity.FhirFilterPatch;

public class SearchParameterQueryEntity {
	public String key;
	public String modifier;
	public String value;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "SearchParameterQueryEntity [key=" + key + ", modifier=" + modifier + ", value=" + value + "]";
	}
	
}
