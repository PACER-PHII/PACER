package edu.gatech.ResultsManager.fhir.processing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseBundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.AdditionalRequestHeadersInterceptor;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.util.BundleUtil;
import edu.gatech.ResultsManager.fhir.config.FhirConfig;
import gatech.edu.STIECR.JSON.CodeableConcept;

@Service
@Primary
public class DirectFhirQueryService {
    Logger log = LoggerFactory.getLogger(DirectFhirQueryService.class);

    private FhirConfig fhirConfig;
	private FhirContext ctx;
	private IGenericClient client;

    @Autowired
    public DirectFhirQueryService(FhirConfig fhirConfig, FhirContext ctx){
        this.fhirConfig = fhirConfig;
		this.ctx = ctx;
        this.client = ctx.newRestfulGenericClient(fhirConfig.getDataServiceUri());
		if(fhirConfig.getDataUser() != null && !fhirConfig.getDataUser().isEmpty() && fhirConfig.getDataPass() != null && !fhirConfig.getDataPass().isEmpty()) {
			client.registerInterceptor(new BasicAuthInterceptor(fhirConfig.getDataUser(),fhirConfig.getDataPass()));
		}
		if(fhirConfig.getEpicClientId() != null && !fhirConfig.getEpicClientId().isEmpty()) {
			AdditionalRequestHeadersInterceptor interceptor = new AdditionalRequestHeadersInterceptor();
			interceptor.addHeaderValue("Epic-Client-Id", fhirConfig.getEpicClientId());
			client.registerInterceptor(interceptor);
		}
    }

    public List<Condition> conditionSearchEncounterDiagnosis(String patientId,List<CodeableConcept> ecrConcepts){
        return conditionSearchWithCategory(patientId, ecrConcepts, "encounter-diagnosis");
    }

    public List<Condition> conditionSearchProblemList(String patientId,List<CodeableConcept> ecrConcepts){
        return conditionSearchWithCategory(patientId, ecrConcepts, "problem-list");
    }

    public List<Condition> conditionSearchWithCategory(String patientId,List<CodeableConcept> ecrConcepts,String category){
        Coding[] codings = ecrConcepts.stream()
            .map(ecr -> convertECRCodeableConceptToFHIRCoding(ecr))
            .toArray(Coding[]::new);
        Bundle returnBundle;
        try{
            returnBundle = client.search()
                .forResource(Condition.class)
                .where(Condition.PATIENT.hasId(patientId))
                .and(Condition.CODE.exactly().codings(codings))
                .and(Condition.CATEGORY.exactly().code(category))
                .returnBundle(Bundle.class)
                .execute();
        }
        //If Server does not support category then try without it
        catch(InvalidRequestException e){
            returnBundle = client.search()
                .forResource(Condition.class)
                .where(Condition.PATIENT.hasId(patientId))
                .and(Condition.CODE.exactly().codings(codings))
                .returnBundle(Bundle.class)
                .execute();
        }
        return retrieveAllPages(returnBundle, Condition.class);
    }

    public Encounter encounterRead(String encounterId){
        Encounter returnEncounter = client.read()
            .resource(Encounter.class)
            .withId(encounterId)
            .execute();
        return returnEncounter;
    }

    public List<Immunization> immunizationSearch(String patientId){
        Bundle returnBundle = client.search()
            .forResource(Immunization.class)
            .where(Immunization.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
        return retrieveAllPages(returnBundle, Immunization.class);
    }

    public Medication medicationRead(String medicationId){
        Medication returnMedication = client.read()
            .resource(Medication.class)
            .withId(medicationId)
            .execute();
        return returnMedication;
    }

    public List<MedicationRequest> medicationRequestSearch(String patientId){
        Bundle returnBundle = client.search()
            .forResource(MedicationRequest.class)
            .where(MedicationRequest.PATIENT.hasId(patientId))
            .returnBundle(Bundle.class)
            .execute();
        return retrieveAllPages(returnBundle, MedicationRequest.class);
    }

    public List<Observation> observationSearchWithCategory(String patientId,String category){
        Bundle returnBundle;
        try{
            returnBundle = client.search()
                .forResource(Observation.class)
                .where(Observation.PATIENT.hasId(patientId))
                .and(Observation.CATEGORY.exactly().code(category))
                .returnBundle(Bundle.class)
                .execute();   
        }
        catch(InvalidRequestException e){
            //Just return no results if no category can be searched in this case
            return new ArrayList<Observation>();
        }
        return retrieveAllPages(returnBundle, Observation.class);
    }

    public Patient patientRead(String patientId){
        Patient returnPatient = client.read()
            .resource(Patient.class)
            .withId(patientId)
            .execute();
        return returnPatient;
    }

    public <T extends IBaseResource> List<T> retrieveAllPages(Bundle bundle, Class<T> clazz){
        List<IBaseResource> resources = new ArrayList<IBaseResource>();
        resources.addAll(BundleUtil.toListOfResources(ctx, bundle));
        while(bundle.getLink(IBaseBundle.LINK_NEXT) != null){
            bundle = client.loadPage()
                .next(bundle)
                .execute();
            resources.addAll(BundleUtil.toListOfResources(ctx, bundle));
        }
        List<T> castedResourceList = resources.stream().map(r -> (T) r).collect(Collectors.toList());
        log.debug("Searched and Found "+castedResourceList.size()+" resources of type "+clazz.getName());
        return castedResourceList;
    }

    public Coding convertECRCodeableConceptToFHIRCoding(CodeableConcept ecrConcept){
        Coding returnCoding = new Coding();
        returnCoding.setCode(ecrConcept.getcode());
        returnCoding.setSystem(ecrConcept.getsystem());
        return returnCoding;
    }
}