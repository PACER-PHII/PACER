package org.opencds.cqf.cql.execution;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorException;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.cqframework.cql.elm.execution.Code;
import org.cqframework.cql.elm.execution.CodeSystemDef;
import org.cqframework.cql.elm.execution.CodeSystemRef;
import org.cqframework.cql.elm.execution.Expression;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.cqframework.cql.elm.execution.Library;
import org.cqframework.cql.elm.execution.ParameterDef;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.opencds.cqf.cql.data.fhir.BaseFhirDataProvider;
import org.opencds.cqf.cql.data.fhir.FhirBundleCursorDstu2;
import org.opencds.cqf.cql.data.fhir.FhirBundleCursorStu3;
import org.opencds.cqf.cql.data.fhir.FhirDataProviderDstu2;
import org.opencds.cqf.cql.data.fhir.FhirDataProviderStu3;
import org.opencds.cqf.cql.elm.execution.CodeEvaluator;
import org.opencds.cqf.cql.elm.execution.CodeSystemRefEvaluator;
import org.opencds.cqf.cql.elm.execution.ConceptEvaluator;
import org.opencds.cqf.cql.terminology.fhir.FhirTerminologyProvider;
import org.opencds.cqf.cql.util.LibraryUtil;
import org.opencds.cqf.cql.util.service.BaseCodeMapperService;
import org.opencds.cqf.cql.util.service.BaseCodeMapperService.CodeMapperIncorrectEquivalenceException;
import org.opencds.cqf.cql.util.service.BaseCodeMapperService.CodeMapperNotFoundException;
import org.opencds.cqf.cql.util.service.FhirCodeMapperServiceStu3;
import org.opencds.cqf.cql.util.service.OAuthTokenService;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.EncodingEnum;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;

/**
 * Created by Christopher on 1/13/2017.
 */
@Path("evaluate")
public class Executor {
	
    private Map<String, List<Integer>> locations = new HashMap<>();

    // for future use
    private ModelManager modelManager;
    private BaseCodeMapperService codeMapperService;
    private ModelManager getModelManager() {
        if (modelManager == null) {
            modelManager = new ModelManager();
        }
        return modelManager;
    }

    private LibraryManager libraryManager;
    private LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            libraryManager = new LibraryManager(getModelManager());
            libraryManager.getLibrarySourceLoader().clearProviders();
            libraryManager.getLibrarySourceLoader().registerProvider(getLibrarySourceProvider());
        }
        return libraryManager;
    }

    private ExecutorLibrarySourceProvider librarySourceProvider;
    private ExecutorLibrarySourceProvider getLibrarySourceProvider() {
        if (librarySourceProvider == null) {
            librarySourceProvider = new ExecutorLibrarySourceProvider();
        }
        return librarySourceProvider;
    }

    private LibraryLoader libraryLoader;
    private LibraryLoader getLibraryLoader() {
        if (libraryLoader == null) {
            libraryLoader = new ExecutorLibraryLoader(getLibraryManager(), getModelManager());
        }
        return libraryLoader;
    }

    private void registerProviders(Context context, String bearerToken, String termSvcUrl, String termUser,
                                   String termPass, String dataPvdrURL, String dataUser,
                                   String dataPass, String codeMapServiceUri, String codeMapperUser,
                                   String codeMapperPass, String fhirVersion, String access_token, String epicClientId)
    {
        // TODO: plugin authorization for data provider when available

        String defaultEndpoint = "http://measure.eval.kanvix.com/cqf-ruler/baseDstu3";

        BaseFhirDataProvider provider = (fhirVersion != null && fhirVersion.equalsIgnoreCase("DSTU2")) ? new FhirDataProviderDstu2() : new FhirDataProviderStu3();
        if(dataUser != null && !dataUser.isEmpty() && dataPass != null && !dataPass.isEmpty()) {
        	provider = provider.withBasicAuth(dataUser,dataPass);
        }
        if(bearerToken != null && !bearerToken.isEmpty()) {
        	bearerToken = bearerToken.split("Bearer ")[1];
        	provider = provider.withBearerAuth(bearerToken);
        }
        if(access_token != null && !access_token.isEmpty()) {
        	IGenericClient client = provider.getFhirClient();
        	BearerTokenAuthInterceptor authInterceptor = new BearerTokenAuthInterceptor(access_token);
        	client.registerInterceptor(authInterceptor);
        }
        provider.setEndpoint(dataPvdrURL == null ? defaultEndpoint : dataPvdrURL);
        FhirContext fhirContext = provider.getFhirContext();
        Map<String, String> additionalHeaders = new HashMap<String, String>();
        if(epicClientId != null && !epicClientId.isEmpty()) {
        	additionalHeaders.put("Epic-Client-ID", epicClientId);
        	IGenericClient client = provider.getFhirClient();
        	AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
			interceptor.addHeaderValue("Epic-Client-ID", epicClientId);
			client.registerInterceptor(interceptor);
        }
        fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
        provider.setFhirContext(fhirContext);
        provider.getFhirClient().setEncoding(EncodingEnum.JSON);
        FhirTerminologyProvider terminologyProvider = new FhirTerminologyProvider()
                .withBasicAuth(termUser, termPass)
                .setEndpoint(termSvcUrl == null ? defaultEndpoint : termSvcUrl, false);
        
        codeMapperService = (codeMapServiceUri != null && !codeMapServiceUri.isEmpty()) ? new FhirCodeMapperServiceStu3()
        		.withBasicAuth(codeMapperUser,codeMapperPass)
        		.setEndpoint(codeMapServiceUri)
        		: null;
        provider.setTerminologyProvider(terminologyProvider);
//        provider.setSearchUsingPOST(true);
        provider.setExpandValueSets(true);
        context.registerDataProvider("http://hl7.org/fhir", provider);
        context.registerTerminologyProvider(terminologyProvider);
        context.registerLibraryLoader(getLibraryLoader());
    }

    private void performRetrieve(String fhirVersion, Iterable result, JSONObject results) {
        FhirContext fhirContext = (fhirVersion != null && fhirVersion.equalsIgnoreCase("DSTU2")) ? FhirContext.forDstu2() : FhirContext.forDstu3(); // for JSON parsing
        Iterator it = result.iterator();
        List<Object> findings = new ArrayList<>();
        while (it.hasNext()) {
            // returning full JSON retrieve response
            findings.add(fhirContext
                    .newJsonParser()
                    .setPrettyPrint(true)
                    .encodeResourceToString((org.hl7.fhir.instance.model.api.IBaseResource)it.next()));
        }
        results.put("result", findings.toString());
    }

    private String resolveType(Object result) {
        String type = result == null ? "Null" : result.getClass().getSimpleName();
        switch (type) {
            case "BigDecimal": return "Decimal";
            case "ArrayList": return "List";
            case "FhirBundleCursor": return "Retrieve";
        }
        return type;
    }

    private CqlTranslator getTranslator(String cql, LibraryManager libraryManager, ModelManager modelManager) {
        return getTranslator(new ByteArrayInputStream(cql.getBytes(StandardCharsets.UTF_8)), libraryManager, modelManager);
    }

    private CqlTranslator getTranslator(InputStream cqlStream, LibraryManager libraryManager, ModelManager modelManager) {
        ArrayList<CqlTranslator.Options> options = new ArrayList<>();
        options.add(CqlTranslator.Options.EnableDateRangeOptimization);
//        options.add(CqlTranslator.Options.EnableAnnotations);
//        options.add(CqlTranslator.Options.EnableDetailedErrors);
        CqlTranslator translator;
        try {
            translator = CqlTranslator.fromStream(cqlStream, modelManager, libraryManager,
                    options.toArray(new CqlTranslator.Options[options.size()]));
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Errors occurred translating library: %s", e.getMessage()));
        }

        String xml = translator.toXml();

        if (translator.getErrors().size() > 0) {
            throw new IllegalArgumentException(errorsToString(translator.getErrors()));
        }

        return translator;
    }

    private String errorsToString(Iterable<CqlTranslatorException> exceptions) {
        ArrayList<String> errors = new ArrayList<>();
        for (CqlTranslatorException error : exceptions) {
            TrackBack tb = error.getLocator();
            String lines = tb == null ? "[n/a]" : String.format("%s[%d:%d, %d:%d]",
                    (tb.getLibrary() != null ? tb.getLibrary().getId() + (tb.getLibrary().getVersion() != null
                            ? ("-" + tb.getLibrary().getVersion()) : "") : ""),
                    tb.getStartLine(), tb.getStartChar(), tb.getEndLine(), tb.getEndChar());
            errors.add(lines + error.getMessage() + "\n");
        }

        return errors.toString();
    }

    private void setExpressionLocations(org.hl7.elm.r1.Library library) {
        if (library.getStatements() == null) return;
        for (org.hl7.elm.r1.ExpressionDef def : library.getStatements().getDef()) {
            int startLine = def.getTrackbacks().isEmpty() ? 0 : def.getTrackbacks().get(0).getStartLine();
            int startChar = def.getTrackbacks().isEmpty() ? 0 : def.getTrackbacks().get(0).getStartChar();
            List<Integer> loc = Arrays.asList(startLine, startChar);
            locations.put(def.getName(), loc);
        }
    }

    private Library readLibrary(InputStream xmlStream) {
        try {
            return CqlLibraryReader.read(xmlStream);
        } catch (IOException | JAXBException e) {
            throw new IllegalArgumentException("Error encountered while reading ELM xml: " + e.getMessage());
        }
    }

    private Library translateLibrary(CqlTranslator translator) {
        return readLibrary(new ByteArrayInputStream(translator.toXml().getBytes(StandardCharsets.UTF_8)));
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON + ";charset=utf-8"})
    public String evaluateCql(@HeaderParam("Authorization") String bearerToken, @HeaderParam("Epic-Client-ID") String epicClientId,
    		@QueryParam("client_id") String client_id, @QueryParam("client_secret") String client_secret,
    		@QueryParam("redirect_uri") String redirect_uri,@QueryParam("code") String oauth_code, String requestData) throws JAXBException, IOException, ParseException {

        JSONParser parser = new JSONParser();
        JSONObject json;
        try {
            json = (JSONObject) parser.parse(requestData);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Error parsing JSON request: " + e.getMessage());
        }

        String code = (String) json.get("code");
        json.remove("code");
        String terminologyServiceUri = (String) json.get("terminologyServiceUri");
        json.remove("terminologyServiceUri");
        String terminologyUser = (String) json.get("terminologyUser");
        json.remove("terminologyUser");
        String terminologyPass = (String) json.get("terminologyPass");
        json.remove("terminologyPass");
        String dataServiceUri = (String) json.get("dataServiceUri");
        json.remove("dataServiceUri");
        String dataUser = (String) json.get("dataUser");
        json.remove("dataUser");
        String dataPass = (String) json.get("dataPass");
        json.remove("dataPass");
        String patientId = (String) json.get("patientId");
        json.remove("patientId");
        String codeMapperServiceUri = (String) json.get("codeMapperServiceUri");
        json.remove("codeMapperServiceUri");
        String codeMapperUser = (String) json.get("codeMapperUser");
        json.remove("codeMapperUser");
        String codeMapperPass = (String) json.get("codeMapperPass");
        json.remove("codeMapperPass");
        JSONObject codeMapperSystemsMap =  (JSONObject) json.get("codeMapperSystemsMap");
        json.remove("codeMapperSystemsMap");
        String fhirVersion = (String) json.get("fhirVersion");
        json.remove("fhirVersion");
        String oauthBaseURI = (String) json.get("oauthBaseURI");
        json.remove("oauthBaseURI");
        String access_token = "";
        if(!Stream.of(client_id, client_secret,redirect_uri,oauth_code,oauthBaseURI).allMatch(Objects::isNull)) {
        	OAuthTokenService oauthTokenService = new OAuthTokenService();
        	access_token = oauthTokenService.getAccessTokenFromOAuth(
    			oauthBaseURI, client_id, client_secret, redirect_uri,
    			oauth_code);
        }
        
        CqlTranslator translator;
        try {
            translator = getTranslator(code, getLibraryManager(), getModelManager());
        }
        catch (IllegalArgumentException iae) {
        	iae.printStackTrace();
            JSONObject result = new JSONObject();
            JSONArray resultArr = new JSONArray();
            result.put("translation-error", iae.getMessage());
            resultArr.add(result);
            return resultArr.toJSONString();
        }

        setExpressionLocations(translator.getTranslatedLibrary().getLibrary());

        if (locations.isEmpty()) {
            JSONObject result = new JSONObject();
            JSONArray resultArr = new JSONArray();
            result.put("result", "Please provide valid CQL named expressions for execution output");
            result.put("name", "No expressions found");
            String location = String.format("[%d:%d]", 0, 0);
            result.put("location", location);

            resultArr.add(result);
            return resultArr.toJSONString();
        }

        Library library = translateLibrary(translator);

        Context context = new Context(library);
        registerProviders(context, bearerToken, terminologyServiceUri, terminologyUser,
        		terminologyPass, dataServiceUri, dataUser, dataPass, codeMapperServiceUri,
        		codeMapperUser, codeMapperPass, fhirVersion, access_token, epicClientId);

        JSONArray resultArr = new JSONArray();
        if(library.getParameters() != null) {
	        for(ParameterDef def:library.getParameters().getDef()) {
	        	if(context.resolveParameterRef(library.getLocalId(), def.getName()) != null) {
	        		context.setParameter(library.getLocalId(), def.getName(), json.get(def.getName()));
	        	}
	        }
        }
        if(codeMapperService != null && codeMapperSystemsMap != null) {
        	for (ExpressionDef def: library.getStatements().getDef()) {
	        	if(def.getExpression() instanceof ConceptEvaluator) {
	        		ConceptEvaluator conceptEval = (ConceptEvaluator) def.getExpression();
	        		for(Code codeConcept:new ArrayList<>(conceptEval.getCode())) {
	        			String systemRefName = codeConcept.getSystem().getName();
	        			String sourceSystemUri = LibraryUtil.getCodeSystemDefFromName(library, systemRefName).getId();
	        			if(codeMapperSystemsMap.get(sourceSystemUri) != null) { 
	        				String targetSystemUri = (String) codeMapperSystemsMap.get(sourceSystemUri);
	        				try {
								List<Code> translatedCodes = codeMapperService.translateCode(codeConcept, sourceSystemUri, targetSystemUri, library);
								List<CodeEvaluator> translatedCodeEvaluators = new ArrayList<CodeEvaluator>();
								for(Code translatedCode:translatedCodes) {
									CodeEvaluator translatedCodeEvaluator = new CodeEvaluator();
									if(translatedCode.getCode() != null) {
										translatedCodeEvaluator.withCode(translatedCode.getCode());
									}
									if(translatedCode.getDisplay() != null) {
										translatedCodeEvaluator.withDisplay(translatedCode.getDisplay());
									}
									if(translatedCode.getSystem() != null) {
										CodeSystemRefEvaluator systemRefEvaluator = new CodeSystemRefEvaluator();
										systemRefEvaluator.withName(translatedCode.getSystem().getName());
										translatedCodeEvaluator.withSystem(systemRefEvaluator);
									}
									translatedCodeEvaluators.add(translatedCodeEvaluator);
								}
								conceptEval.getCode().remove(codeConcept);
								conceptEval.getCode().addAll(translatedCodeEvaluators);
							} catch (CodeMapperIncorrectEquivalenceException | CodeMapperNotFoundException e) {
							}
	        			}
	        		}
	        	}
        	}
        }
        for (ExpressionDef def : library.getStatements().getDef()) {
        	if(def.getName().equalsIgnoreCase("Patient")) {
        		continue;
        	}
        	//Continue on executing statements afterwords!
            context.enterContext(def.getContext());
            if (patientId != null && !patientId.isEmpty()) {
                context.setContextValue(context.getCurrentContext(), patientId);
            }
            else {
                context.setContextValue(context.getCurrentContext(), "null");
            }
            JSONObject result = new JSONObject();

            try {
                result.put("name", def.getName());

                String location = String.format("[%d:%d]", locations.get(def.getName()).get(0), locations.get(def.getName()).get(1));
                result.put("location", location);

                Object res = def instanceof FunctionDef ? "Definition successfully validated" : def.getExpression().evaluate(context);

                if (res == null) {
                    result.put("result", "Null");
                }
                else if (res instanceof FhirBundleCursorStu3) {
                    performRetrieve(fhirVersion,(Iterable) res, result);
                }
                else if (res instanceof FhirBundleCursorDstu2) {
                    performRetrieve(fhirVersion,(Iterable) res, result);
                }
                else if (res instanceof List) {
                    if (((List) res).size() > 0 && ((List) res).get(0) instanceof IBaseResource) {
                        performRetrieve(fhirVersion,(Iterable) res, result);
                    }
                    else {
                        result.put("result", res.toString());
                    }
                }
                else if (res instanceof IBaseResource) {
                    result.put("result", FhirContext.forDstu3().newJsonParser().setPrettyPrint(true).encodeResourceToString((IBaseResource) res));
                }
                else {
                    result.put("result", res.toString());
                }
                result.put("resultType", resolveType(res));
            }
            catch (RuntimeException re) {
                result.put("error", re.getMessage());
                re.printStackTrace();
            }
            resultArr.add(result);
        }
        return resultArr.toJSONString();
    }

}
