package org.opencds.cqf.cql.util.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.Retrieve;

public class FhirSearchRequirementsService {
	
	public FhirSearchRequirementsService() {
		
	}

	public Map<String, Set<String> > getSearchRequirementsOfLibrary(Library library){
		Map<String, Set<String> > spMap = new HashMap<String, Set<String> >();
		for (ExpressionDef expressionDef: library.getStatements().getDef()) {
			if(expressionDef.getExpression() instanceof Retrieve) {
				Retrieve retreieve = (Retrieve)expressionDef.getExpression();
				QName resourceQName = retreieve.getDataType();
				String resourceString = resourceQName.getLocalPart();
				if(!spMap.containsKey(resourceString)) {
					spMap.put(resourceString, new HashSet<String>());
				}
				Set<String> searchParameterSet = spMap.get(resourceString);
				String codeProperty = retreieve.getCodeProperty();
				testAndAddProperty(retreieve.getCodeProperty(), searchParameterSet);
				testAndAddProperty(retreieve.getDateProperty(), searchParameterSet);
				testAndAddProperty(retreieve.getDateLowProperty(), searchParameterSet);
				testAndAddProperty(retreieve.getDateHighProperty(), searchParameterSet);
			}
		}
		return spMap;
	}
	
	public Map<String, Map<String, Object> > getSearchRequirementsOfLibraryPerDefinition(Library library){
		Map<String, Map<String, Object> > definitionNeedsMap = new HashMap<String, Map<String, Object> >();
		for (ExpressionDef expressionDef: library.getStatements().getDef()) {
			if(expressionDef.getExpression() instanceof Retrieve) {
				String expressionName = expressionDef.getName();
				Map<String, Object> definitionNeeds = new HashMap<String, Object>();
				Retrieve retrieve = (Retrieve)expressionDef.getExpression();
				QName resourceQName = retrieve.getDataType();
				String resourceString = resourceQName.getLocalPart();
				definitionNeeds.put("resourceName", resourceString);
				Set<String> searchParameterSet = new HashSet<String>();
				String codeProperty = retrieve.getCodeProperty();
				testAndAddProperty(retrieve.getCodeProperty(), searchParameterSet);
				testAndAddProperty(retrieve.getDateProperty(), searchParameterSet);
				testAndAddProperty(retrieve.getDateLowProperty(), searchParameterSet);
				testAndAddProperty(retrieve.getDateHighProperty(), searchParameterSet);
				definitionNeeds.put("searchParametersUsed",searchParameterSet);
				definitionNeedsMap.put(expressionName, definitionNeeds);
			}
		}
		return definitionNeedsMap;
	}
	
	public Set<String> testAndAddProperty(String property, Set<String> searchParameterSet){
		if(property != null && !property.isEmpty() && !searchParameterSet.contains(property)) {
			searchParameterSet.add(property.toLowerCase());
		}
		return searchParameterSet;
	}	
}