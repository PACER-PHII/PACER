package org.opencds.cqf.cql.util.entity.FhirFilterPatch;

import java.util.Map;

public class ParameterMapEntity {
	Map<String, Map<String, ParameterEntity> > parameterMap;

	public Map<String, Map<String, ParameterEntity> > getParameterMap() {
		return parameterMap;
	}

	public void setParameterMap(Map<String, Map<String, ParameterEntity> > parameterMap) {
		this.parameterMap = parameterMap;
	}

	@Override
	public String toString() {
		return "ParameterMapEntity [parameterMap=" + parameterMap + "]";
	}
	
}