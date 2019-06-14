package edu.gatech.CQLValueSet.Writer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.gatech.CQLValueSetExpander.model.output.TermSet;

public class TermSetWriter {
	private static String systemDefinitionTemplate = "codesystem \"<codesystem_name>\": '<codesystem_value>'\n";
	private static String conceptSetTemplate = "define \"<valueset_name>\": Concept { <codelist> }\n";
	private static String singleConceptTemplate = "Code '<code_value>' from <system_name>";
	
	public static String convertAllTermSetToCQL(List<TermSet> termsets) {
		Set<String> systemsWritten = new HashSet();
		StringWriter outputWriter = new StringWriter();
		for(TermSet termset:termsets) {
			try {
				String line = convertTermSetToString(termset,systemsWritten);
				outputWriter.append(line);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return outputWriter.toString();
	}
	public static String convertTermSetToString(TermSet termset,Set<String> systemsWritten) throws IOException {
		StringWriter stringWriter = new StringWriter();
		BufferedWriter outputWriter = new BufferedWriter(stringWriter);
		for(String system:termset.getSystems()) {
			if(!systemsWritten.contains(system)) {
				systemsWritten.add(system);
				String systemIdentifier = getSystemIdentifierFromSystem(system);
				String finalSystemString =  systemDefinitionTemplate.replaceFirst("<codesystem_name>", systemIdentifier)
												.replaceFirst("<codesystem_value>", system);
				outputWriter.write(finalSystemString);
			}
		}
		String codeListString = "";
		StringBuilder codeListBuilder = new StringBuilder();
		for(String system:systemsWritten) {
			if(termset.getCodeForSystem(system) != null) {
				for(String code:termset.getCodeForSystem(system)) {
					codeListBuilder.append(singleConceptTemplate.replaceFirst("<code_value>", code).replaceFirst("<system_name>", getSystemIdentifierFromSystem(system)));
					codeListBuilder.append(", ");
				}
			}
		}
		codeListString = codeListBuilder.toString();
		codeListString = codeListString.substring(0, Math.max(0, codeListString.length() - 2)); //Clip trailing comma space
		outputWriter.write(conceptSetTemplate.replace("<valueset_name>", termset.getName()).replace("<codelist>", codeListString));
		outputWriter.flush();
		outputWriter.close();
		return stringWriter.toString();
	}
	
	private static String getSystemIdentifierFromSystem(String system) {
		String systemIdentifier = system;
		if(systemIdentifier.contains("/")) {
			systemIdentifier = systemIdentifier.substring(systemIdentifier.lastIndexOf('/') + 1);
		}
		return systemIdentifier;
	}
}