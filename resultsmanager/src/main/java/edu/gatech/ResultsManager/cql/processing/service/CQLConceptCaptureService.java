package edu.gatech.ResultsManager.cql.processing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import gatech.edu.STIECR.JSON.CodeableConcept;
import java.util.Objects;

/**
 * This service reads a cql file and captures all codes as ECR codeableconcepts
 * and all concepts as a map of strings to lists of ECR codeableconcepts
 */
@Service
@Primary
public class CQLConceptCaptureService {

    private static String codesystemDefCapturePatternString = "codesystem\\s*\\\"(?<CodeystemIdentifier>.*)\\\":\\s*\\'(?<CodesystemURL>.*)\\'";
	private static String conceptCapturePatternString = "define \\\"(?<ConceptDefinition>.*)\\\":\\s*Concept\\s*\\{(?<ConceptCodes>.*)\\}";
	private static String codeListSeperatorPatternString = "(.+?)(,(?=\\s*Code)|$)";
	private static String codeSystemAndDisplayCapturePatternString = "Code\\s*\\'(?<CodeName>.*)\\'\\s*from\\s*(?<CodesystemRef>\\w*)\\s*(display\\s*\\'(?<DisplayName>.*)\\')?";
    private static Pattern codesystemDefCapturePattern = Pattern.compile(codesystemDefCapturePatternString);
	private static Pattern conceptCapturePattern = Pattern.compile(conceptCapturePatternString);
	private static Pattern codeListSeperatorPattern = Pattern.compile(codeListSeperatorPatternString);
	private static Pattern codeSystemAndDisplayCapturePattern = Pattern.compile(codeSystemAndDisplayCapturePatternString);
    Map<String, String> codesystemMap = new HashMap<String, String>();
    Map<String, List<CodeableConcept> > conceptDefMap = new HashMap<String, List<CodeableConcept> >();
	List<CodeableConcept> allCodes = new ArrayList<CodeableConcept>();

	public CQLConceptCaptureService(Map<String,String> codesystemMap, Map<String,List<CodeableConcept>> conceptDefMap) {
		this.codesystemMap = codesystemMap;
		this.conceptDefMap = conceptDefMap;
	}

    public CQLConceptCaptureService(){

    }

    public void processCQL(String cqlString){
		Matcher codesystemDefMatcher = codesystemDefCapturePattern.matcher(cqlString);
		while(codesystemDefMatcher.find()){
			codesystemMap.put(codesystemDefMatcher.group("CodeystemIdentifier"), codesystemDefMatcher.group("CodesystemURL"));
		}
		Map<String, String> conceptStringMap = new HashMap<String, String>();
		Matcher conceptCaptureMatcher = conceptCapturePattern.matcher(cqlString);
		while(conceptCaptureMatcher.find()){
			conceptStringMap.put(conceptCaptureMatcher.group("ConceptDefinition"), conceptCaptureMatcher.group("ConceptCodes"));
		}
		for(Entry<String, String> entry:conceptStringMap.entrySet()){
			String conceptName = entry.getKey();
			String codeListString = entry.getValue();
			Matcher codeListSeperatorMatcher = codeListSeperatorPattern.matcher(codeListString);
			List<String> codeSet = new ArrayList<String>();
			while(codeListSeperatorMatcher.find()){
				codeSet.add(codeListSeperatorMatcher.group(0));
			}
			List<CodeableConcept> codeableConceptSet = new ArrayList<CodeableConcept>();
			for(String codeDef:codeSet){
				Matcher codeSystemAndDisplayMatcher = codeSystemAndDisplayCapturePattern.matcher(codeDef);
				while(codeSystemAndDisplayMatcher.find()){
					CodeableConcept cc = new CodeableConcept();
					cc.setcode(codeSystemAndDisplayMatcher.group("CodeName"));
					String codesystemRef = codeSystemAndDisplayMatcher.group("CodesystemRef");
					if(codesystemRef != null && !codesystemRef.isEmpty()){
						String codesystemURL = codesystemMap.get(codesystemRef);
						cc.setsystem(codesystemURL);
					}
					cc.setsystem(codeSystemAndDisplayMatcher.group("CodesystemRef"));
					if(codesystemMap.containsKey(cc.getsystem())){
						cc.setsystem(codesystemMap.get(cc.getsystem()));
					}
					if(codeSystemAndDisplayMatcher.group("DisplayName") != null){
						cc.setdisplay(codeSystemAndDisplayMatcher.group("DisplayName"));
					}
					codeableConceptSet.add(cc);
				}
			}
			allCodes.addAll(codeableConceptSet);
			conceptDefMap.put(conceptName, codeableConceptSet);
		}
    }
	
	public Map<String,String> getCodesystemMap() {
		return this.codesystemMap;
	}

	public Map<String,List<CodeableConcept>> getConceptDefMap() {
		return this.conceptDefMap;
	}

	public List<CodeableConcept> getAllCodes() {
		return this.allCodes;
	}

}