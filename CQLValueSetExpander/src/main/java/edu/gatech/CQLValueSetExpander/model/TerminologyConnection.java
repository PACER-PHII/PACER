package edu.gatech.CQLValueSetExpander.model;

import java.net.URL;

public class TerminologyConnection {
	private URL url;
	private String username;
	private String password;
	
	public TerminologyConnection(URL url, String username, String password) {
		super();
		this.url = url;
		this.username = username;
		this.password = password;
	}
	
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
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
		return "TerminologyConnection [url=" + url + ", username=" + username + ", password=" + password + "]";
	}
}