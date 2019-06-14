package edu.gatech.CQLValueSetExpander.model.output;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TermSet {
	private Map<String,Set<String> > systemCodeMap;
	private String name;
	
	public TermSet(String name) {
		super();
		systemCodeMap = new HashMap<String,Set<String> >();
		this.name = name;
	}
	
	public Map<String,Set<String> > addCode(String system,String code){
		Set<String> codes = systemCodeMap.get(system);
		if(codes == null) {
			codes = new HashSet<String>();
			systemCodeMap.put(system, codes);
		}
		codes.add(code);
		return systemCodeMap;
	}
	
	public Set<String> getCodeForSystem(String system){
		return systemCodeMap.get(system);
	}
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Set<String> getSystems(){
		return systemCodeMap.keySet();
	}
}