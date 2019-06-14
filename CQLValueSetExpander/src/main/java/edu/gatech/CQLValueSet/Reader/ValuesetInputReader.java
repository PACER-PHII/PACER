package edu.gatech.CQLValueSet.Reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.gatech.CQLValueSetExpander.model.ValuesetInput;

public class ValuesetInputReader {
	private static final String valuesetLineRegex = "valueset\\s*\"(\\w*)\":\\s*'(.*)'";
	private static final Pattern valuesetLinePattern = Pattern.compile(valuesetLineRegex);
	
	public static List<ValuesetInput> parseValueSets(FileInputStream input) throws UnsupportedEncodingException, IOException{
		List<ValuesetInput> returnList = new ArrayList<ValuesetInput>();
		Matcher matcher = valuesetLinePattern.matcher(new String(input.readAllBytes(), "UTF-8"));
		while(matcher.find()) {
			ValuesetInput valuesetInput = new ValuesetInput(matcher.group(1),matcher.group(2));
			returnList.add(valuesetInput);
		}
		return returnList;
	}
}