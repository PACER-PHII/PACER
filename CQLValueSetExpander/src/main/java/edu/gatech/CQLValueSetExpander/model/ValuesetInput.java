package edu.gatech.CQLValueSetExpander.model;

public class ValuesetInput {
	private String name;
	private String value;
	
	public ValuesetInput(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "ValuesetInput [name=" + name + ", value=" + value + "]";
	}
	public ValuesetInput() {
		super();
	}
	
	
}
