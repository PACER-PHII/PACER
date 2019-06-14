package edu.gatech.CQLValueSetExpander.converter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import com.beust.jcommander.IStringConverter;

public class URLConverter implements IStringConverter<URL>{

	public URL convert(String value) {
		// TODO Auto-generated method stub
		try {
			return new URL(value);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
