package org.opencds.cqf.cql.util.entity.FhirFilterPatch;

public class ParameterEntity {
	public String name;
	public String type;
	public String path;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	@Override
	public String toString() {
		return "ParameterEntity [name=" + name + ", type=" + type + ", path=" + path + "]";
	}
	
}
