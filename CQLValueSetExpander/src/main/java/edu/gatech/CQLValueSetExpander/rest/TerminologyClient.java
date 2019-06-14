package edu.gatech.CQLValueSetExpander.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.gatech.CQLValueSetExpander.model.TerminologyConnection;
import edu.gatech.CQLValueSetExpander.model.ValuesetInput;
import edu.gatech.CQLValueSetExpander.model.output.TermSet;

public class TerminologyClient {
	public TerminologyConnection connection;
	private HttpClient client;
	
	private static String requestURITemplate = "ValueSet/<valueset>/$expand";

	public TerminologyClient(TerminologyConnection connection) {
		super();
		this.connection = connection;
		this.client = new DefaultHttpClient();
	}
	
	public TerminologyClient(URL url, String username,String password) {
		super();
		this.connection = new TerminologyConnection(url,username,password);
		BasicCredentialsProvider credentialProvdier = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,password);
		credentialProvdier.setCredentials(AuthScope.ANY, credentials);
		this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(credentialProvdier).build();
	}
	
	public TermSet getTerminologySet(ValuesetInput valueset) throws JsonProcessingException, IOException, URISyntaxException {
		HttpResponse response = executeRequest(valueset);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
			   + response.getStatusLine().getStatusCode());
		}
		TermSet termset = createTermSetFromResponse(valueset,response);
		return termset;
	}
	
	private HttpResponse executeRequest(ValuesetInput valueset) throws URISyntaxException, ClientProtocolException, IOException {
		String templatedPath = requestURITemplate.replaceFirst("<valueset>", valueset.getValue());
		URL baseURL = connection.getUrl();
		URI uri = baseURL.toURI();
		URI fullUri = uri.resolve(uri.getPath() + "/" + templatedPath);
		fullUri.normalize();
		HttpGet request = new HttpGet(fullUri.toURL().toString());
		request.addHeader("Accept", "application/fhir+json;q=1.0, application/json+fhir");
		return client.execute(request);
	}
	
	private TermSet createTermSetFromResponse(ValuesetInput valueset,HttpResponse response) throws JsonProcessingException, IOException {
		JsonNode json = readJsonFromResponse(response);
		TermSet termSet = new TermSet(valueset.getName());
		for(JsonNode jsonCode:(ArrayNode)json.get("expansion").get("contains")) {
			String system = jsonCode.get("system").asText();
			String code = jsonCode.get("code").asText();
			termSet.addCode(system, code);
		}
		return termSet;
	}
	
	private JsonNode readJsonFromResponse(HttpResponse response) throws JsonProcessingException, IOException {
		BufferedReader responseStream = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while((inputStr = responseStream.readLine()) != null) {
			responseStrBuilder.append(inputStr);
		}
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(responseStrBuilder.toString());
	}
}