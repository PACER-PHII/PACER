package edu.gatech.ResultsManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import gatech.edu.STIECR.JSON.CodeableConcept;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ResultsManagerApplicationTests {

	String codesystemDefCapturePatternString = "codesystem\\s*\\\"(?<CodeystemIdentifier>.*)\\\":\\s*\\'(?<CodesystemURL>.*)\\'";
	String conceptCapturePatternString = "define \\\"(?<ConceptDefinition>.*)\\\":\\s*Concept\\s*\\{(?<ConceptCodes>.*)\\}";
	String codeListSeperatorPatternString = "(.+?)(,(?=\\s*Code)|$)";
	String codeSystemAndDisplayCapturePatternString = "Code\\s*\\'(?<CodeName>.*)\\'\\s*from\\s*(?<CodesystemRef>\\w*)\\s*(display\\s*\\'(?<DisplayName>.*)\\')?";
	Pattern codesystemDefCapturePattern = Pattern.compile(codesystemDefCapturePatternString);
	Pattern conceptCapturePattern = Pattern.compile(conceptCapturePatternString);
	Pattern codeListSeperatorPattern = Pattern.compile(codeListSeperatorPatternString);
	Pattern codeSystemAndDisplayCapturePattern = Pattern.compile(codeSystemAndDisplayCapturePatternString);
	@Test
	public void testRegex() {
		ClassLoader classLoader = getClass().getClassLoader();
		File cqlFile = new File(classLoader.getResource("ECR.cql").getFile());
		String cqlString = "";
		try {
			cqlString = FileUtils.readFileToString(cqlFile, "UTF-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		System.out.println("-----------------------CODESYSTEM IDENTIFIERS--------------------");
		Map<String, String> codesystemMap = new HashMap<String, String>();
		Matcher codesystemDefMatcher = codesystemDefCapturePattern.matcher(cqlString);
		while(codesystemDefMatcher.find()){
			System.out.println("---");
			System.out.println("CodeSystemIdentifier:"+codesystemDefMatcher.group("CodeystemIdentifier"));
			System.out.println("CodesystemURL:"+codesystemDefMatcher.group("CodesystemURL"));
			codesystemMap.put(codesystemDefMatcher.group("CodeystemIdentifier"), codesystemDefMatcher.group("CodesystemURL"));
		}
		System.out.println("-----------------------CONCEPT CAPTURE--------------------");
		Map<String, String> conceptStringMap = new HashMap<String, String>();
		Matcher conceptCaptureMatcher = conceptCapturePattern.matcher(cqlString);
		while(conceptCaptureMatcher.find()){
			System.out.println("---");
			System.out.println("ConceptDefinition:"+conceptCaptureMatcher.group("ConceptDefinition"));
			System.out.println("ConceptCodes:"+conceptCaptureMatcher.group("ConceptCodes"));
			conceptStringMap.put(conceptCaptureMatcher.group("ConceptDefinition"), conceptCaptureMatcher.group("ConceptCodes"));
		}
		System.out.println("-----------------------CODEABLE CONCEPT MAKER--------------------");
		Map<String, List<CodeableConcept> > conceptDefMap = new HashMap<String, List<CodeableConcept> >();
		for(Entry<String, String> entry:conceptStringMap.entrySet()){
			String conceptName = entry.getKey();
			String codeListString = entry.getValue();
			Matcher codeListSeperatorMatcher = codeListSeperatorPattern.matcher(codeListString);
			List<String> codeSet = new ArrayList<String>();
			while(codeListSeperatorMatcher.find()){
				System.out.println("---");
				for(int i=0;i<codeListSeperatorMatcher.groupCount();i++){
					codeSet.add(codeListSeperatorMatcher.group(i));
				}
			}
			System.out.println("-----------------------CONCEPT "+conceptName+" --------------------");
			List<CodeableConcept> codeableConceptSet = new ArrayList<CodeableConcept>();
			for(String codeDef:codeSet){
				Matcher codeSystemAndDisplayMatcher = codeSystemAndDisplayCapturePattern.matcher(codeDef);
				while(codeSystemAndDisplayMatcher.find()){
					System.out.println("---");
					CodeableConcept cc = new CodeableConcept();
					System.out.println("Full Line:"+codeDef);
					System.out.println("CodeName:"+codeSystemAndDisplayMatcher.group("CodeName"));
					cc.setcode(codeSystemAndDisplayMatcher.group("CodeName"));
					System.out.println("CodesystemRef:"+codeSystemAndDisplayMatcher.group("CodesystemRef"));
					String codesystemRef = codeSystemAndDisplayMatcher.group("CodesystemRef");
					if(codesystemRef != null && !codesystemRef.isEmpty()){
						String codesystemURL = codesystemMap.get(codesystemRef);
						cc.setsystem(codesystemURL);
					}
					cc.setsystem(codeSystemAndDisplayMatcher.group("CodesystemRef"));
					if(codeSystemAndDisplayMatcher.group("DisplayName") != null){
						System.out.println("DisplayName:"+codeSystemAndDisplayMatcher.group("DisplayName"));	
						cc.setdisplay(codeSystemAndDisplayMatcher.group("DisplayName"));
					}
					codeableConceptSet.add(cc);
				}
			}
			conceptDefMap.put(conceptName, codeableConceptSet);
		}
	}
}