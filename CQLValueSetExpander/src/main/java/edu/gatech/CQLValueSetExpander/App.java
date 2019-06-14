package edu.gatech.CQLValueSetExpander;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.fasterxml.jackson.core.JsonProcessingException;

import edu.gatech.CQLValueSet.Reader.ValuesetInputReader;
import edu.gatech.CQLValueSet.Writer.TermSetWriter;
import edu.gatech.CQLValueSetExpander.model.Args;
import edu.gatech.CQLValueSetExpander.model.ValuesetInput;
import edu.gatech.CQLValueSetExpander.model.output.TermSet;
import edu.gatech.CQLValueSetExpander.rest.TerminologyClient;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String... args)
    {
        Args inputArgs = new Args();
        JCommander.newBuilder()
        	.addObject(inputArgs)
        	.build()
        	.parse(args);
        
        //Check params
        //Read in file
        FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(inputArgs.getInput());
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
        //Read in valueset
        List<ValuesetInput> valuesetInputs = null;
        try {
			valuesetInputs = ValuesetInputReader.parseValueSets(inputStream);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
        //connect to terminology server
      //Pick out results
        TerminologyClient client = new TerminologyClient(inputArgs.getHost(),inputArgs.getUsername(),inputArgs.getPassword());
        List<TermSet> termsets = new ArrayList<TermSet>();
        for(ValuesetInput valueset:valuesetInputs) {
        	try {
				termsets.add(client.getTerminologySet(valueset));
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //Translate to codesystem block
        String output = TermSetWriter.convertAllTermSetToCQL(termsets);
        System.out.print(output);
        
        FileOutputStream outputStream = null;
        try {
			outputStream = new FileOutputStream(inputArgs.getOutput());
			outputStream.write(output.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
