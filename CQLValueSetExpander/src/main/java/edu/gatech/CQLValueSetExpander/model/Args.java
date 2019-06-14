package edu.gatech.CQLValueSetExpander.model;

import java.io.File;
import java.net.URL;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import com.beust.jcommander.converters.URLConverter;

public class Args {

	@Parameter(names = {"-input", "-inputfile", "-i"},
			description = "Input .cql file to be expanded",
			converter = FileConverter.class)
	private File input = new File("");
	
	@Parameter(names = {"-output", "-outputfile", "-o"},
			description = "Output file to be created",
			converter = FileConverter.class)
	private File output = new File("");
	
	@Parameter(names = {"-format", "-f"},
			description = "format of output file to be created")
	private String format = "";
	
	@Parameter(names = {"-host", "-url"},
			description = "URL of terminology server",
			converter = URLConverter.class)
	private URL host = null;
	
	@Parameter(names = {"-username", "-user", "-u"},
			description = "username on terminology server")
	private String username = "";
	
	@Parameter(names = {"-password", "-pass", "-p"},
			description = "password on terminology server",
			password = true)
	private String password = "";

	public File getInput() {
		return input;
	}

	public void setInput(File input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public URL getHost() {
		return host;
	}

	public void setHost(URL host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Args [input=" + input + ", output=" + output + ", format=" + format + ", host=" + host + ", username="
				+ username + ", password=" + password + "]";
	}
}
