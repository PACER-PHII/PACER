package edu.gatech.ResultsManager.FHIR2ECR.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Dosage;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Extension;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Immunization;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Range;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SimpleQuantity;
import org.hl7.fhir.r4.model.Type;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.Dosage.DosageDoseAndRateComponent;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.r4.model.Timing.TimingRepeatComponent;
import org.hl7.fhir.r4.model.codesystems.DoseRateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.gatech.ResultsManager.FHIR2ECR.util.HAPIFHIRUtil;
import edu.gatech.ResultsManager.cql.processing.service.CQLConceptCaptureService;
import edu.gatech.ResultsManager.fhir.processing.DirectFhirQueryService;
import gatech.edu.STIECR.JSON.CodeableConcept;
import gatech.edu.STIECR.JSON.Diagnosis;
import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.ImmunizationHistory;
import gatech.edu.STIECR.JSON.Medication;
import gatech.edu.STIECR.JSON.utils.DateUtil;

@Service
public class DirectFhirECRCreator {
    
    Logger log = LoggerFactory.getLogger(DirectFhirECRCreator.class);
    DirectFhirQueryService directFhirQueryService;
    CQLConceptCaptureService cqlConceptCaptureService;

    @Autowired
    public DirectFhirECRCreator(DirectFhirQueryService directFhirQueryService, CQLConceptCaptureService cqlConceptCaptureService){
        this.directFhirQueryService = directFhirQueryService;
        this.cqlConceptCaptureService = cqlConceptCaptureService;
    }
    //Main Conversion Function
    public ECR queryFhirServerAndCreateECR(String patientResourceId){
        ECR ecr = new ECR();
        mapPatientData(ecr, patientResourceId);
        collectAndMapDiagnosis(ecr, patientResourceId);
        collectAndMapSymptoms(ecr, patientResourceId);
        collectAndMapImmunization(ecr, patientResourceId);
        collectAndMapMedicationRequest(ecr, patientResourceId);
        //mapObservationData(ecr, patientResourceId);
        return ecr;
    }
    //Mapping Resource Functions
    public ECR mapPatientData(ECR ecr, String patientResourceId){
        Patient patient = directFhirQueryService.patientRead(patientResourceId);
        if(patient.getBirthDate() != null) {
			ecr.getPatient().setbirthDate(patient.getBirthDate().toString());
		}
		Type deceasedValue = patient.getDeceased();
		if (deceasedValue != null && deceasedValue instanceof DateTimeType) {
			String deathDate = DateUtil.dateToStdString(((DateTimeType) deceasedValue).getValue());
			ecr.getPatient().setdeathDate(deathDate);
		}
        HumanName name = patientFindBestName(patient);
        ecr.getPatient().getname().setfamily(name.getFamily());
        ecr.getPatient().getname().setgiven(name.getGivenAsSingleString());
		for(Extension extension:patient.getExtension()) {
			if(extension.getUrlElement().equals("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")) {
                List<String> raceCodes = new ArrayList<String>();
				for(Extension subExtension:extension.getExtension()){
					if(subExtension.getUrl().equalsIgnoreCase("ombCategory")){
						//Todo: handle multiple ombCategory assignings later
						Coding coding = (Coding)subExtension.getValue();
						ecr.getPatient().setrace(new gatech.edu.STIECR.JSON.CodeableConcept(coding.getSystem(),coding.getDisplay(),coding.getCode()));
                        raceCodes.add(coding.getCode());
					}
                    if(raceCodes.size() > 0){
                        ecr.getPatient().getrace().setcode(raceCodes.stream().collect(Collectors.joining(",")));
                    }
				}
			}
			if(extension.getUrlElement().equals("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")) {
				for(Extension subExtension:extension.getExtension()){
                    List<String> ethnicityCodes = new ArrayList<String>();
					if(subExtension.getUrl().equalsIgnoreCase("ombCategory")){
						//Todo: handle multiple ombCategory assignings later
						Coding coding = (Coding)subExtension.getValue();
						ecr.getPatient().setethnicity(new gatech.edu.STIECR.JSON.CodeableConcept(coding.getSystem(),coding.getDisplay(),coding.getCode()));
                        ethnicityCodes.add(coding.getCode());
					}
                    if(ethnicityCodes.size() > 0){
                        ecr.getPatient().getrace().setcode(ethnicityCodes.stream().collect(Collectors.joining(",")));
                    }
				}
			}
		}
		PatientCommunicationComponent preferredCommunication = null;
		for(PatientCommunicationComponent communication:patient.getCommunication()) {
			if(communication.getPreferred()) {
				preferredCommunication = communication;
			}
		}
		if(preferredCommunication == null && !patient.getCommunication().isEmpty()) {
			preferredCommunication = patient.getCommunicationFirstRep();
		}
		if(preferredCommunication != null && !preferredCommunication.getLanguage().isEmpty()) {
			Coding coding = preferredCommunication.getLanguage().getCodingFirstRep();
			ecr.getPatient().setpreferredLanguage(new gatech.edu.STIECR.JSON.CodeableConcept(coding.getSystem(),coding.getDisplay(),coding.getCode()));
		}
		if(!patient.getAddress().isEmpty()) {
			Address address = patientFindBestAddress(patient);
            if(address != null){
                String addressString = HAPIFHIRUtil.addressToString(address);
			    ecr.getPatient().setstreetAddress(addressString);
            }
		}
		if(patient.getGender() != null) {
			ecr.getPatient().setsex(patient.getGender().toCode());
		}
        return ecr;
    }

    public ECR collectAndMapDiagnosis(ECR ecr, String patientResourceId){
        Map<String, List<CodeableConcept> > conceptMap = cqlConceptCaptureService.getConceptDefMap();
        List<String> conditionConcepts = List.of("Chlamydia_Codes_0","Chlamydia_Codes_1","Chlamydia_Codes_2","Gonorrhea_Codes_0","Gonorrhea_Codes_1","Gonorrhea_Codes_2");
        for(String concept:conditionConcepts){
            List<CodeableConcept> conditionCodes = conceptMap.get(concept);
            List<Condition> conditionED = directFhirQueryService.conditionSearchEncounterDiagnosis(patientResourceId, conditionCodes);
            List<Condition> conditionPL = directFhirQueryService.conditionSearchProblemList(patientResourceId, conditionCodes);
            List<Condition> allConditions = new ArrayList<Condition>();
            allConditions.addAll(conditionED);
            allConditions.addAll(conditionPL);
            for(Condition c:allConditions){
                mapDiagnosis(ecr,c);
            }
        }
        return ecr;
    }

    public ECR collectAndMapSymptoms(ECR ecr, String patientResourceId){
        Map<String, List<CodeableConcept> > conceptMap = cqlConceptCaptureService.getConceptDefMap();
        List<String> conditionConcepts = List.of("Sti Symptoms");
        for(String concept:conditionConcepts){
            List<CodeableConcept> conditionCodes = conceptMap.get(concept);
            List<Condition> conditionED = directFhirQueryService.conditionSearchEncounterDiagnosis(patientResourceId, conditionCodes);
            List<Condition> conditionPL = directFhirQueryService.conditionSearchProblemList(patientResourceId, conditionCodes);
            List<Condition> allConditions = new ArrayList<Condition>();
            allConditions.addAll(conditionED);
            allConditions.addAll(conditionPL);
            for(Condition c:allConditions){
                mapSymptom(ecr,c);
            }
        }
        return ecr;
    }

    public ECR mapDiagnosis(ECR ecr, Condition condition){
        Diagnosis diagnosis = new Diagnosis();
		if(condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
            CodeableConcept cc = findCCfromCode(condition.getCode());
            if(cc != null){
                diagnosis.setCode(cc.getcode());
                diagnosis.setSystem(cc.getsystem());
                diagnosis.setDisplay(cc.getdisplay());
            }
		}
		if(condition.getOnset() != null) {
            if(condition.hasOnsetDateTimeType()){
                diagnosis.setDate(HAPIFHIRUtil.getDate(condition.getOnsetDateTimeType()).toString());
            }
            else if(condition.hasOnsetPeriod()){
                diagnosis.setDate(condition.getOnsetPeriod().getStart().toString());
            }
		}
        //Handle Related Encounters
        if(condition.hasEncounter()){
            String encounterId = condition.getEncounter().getReferenceElement().getIdPart();
            Encounter encounter = directFhirQueryService.encounterRead(encounterId);
            if(encounter.hasPeriod()){
                ecr.getPatient().setvisitDateTime(encounter.getPeriod().getStart().toString());
                ecr.getPatient().setdateDischarged(encounter.getPeriod().getEnd().toString());
            }
        }
        ecr.getPatient().getDiagnosis().add(diagnosis);
        return ecr;
    }

    public ECR mapSymptom(ECR ecr, Condition condition){
        CodeableConcept cc = new CodeableConcept();
        //TODO:Check to see if the symptom is around the time of the visit
		if(condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
            cc = findCCfromCode(condition.getCode());
            ecr.getPatient().getsymptoms().add(cc);
		}
        return ecr;
    }

    public ECR collectAndMapImmunization(ECR ecr, String patientResourceId){
        List<Immunization> immunizations = directFhirQueryService.immunizationSearch(patientResourceId);
        for(Immunization i:immunizations){
            mapImmunization(ecr,i);
        }
        return ecr;
    }

    public ECR mapImmunization(ECR ecr, Immunization immunization){
        ImmunizationHistory ecrImmunization = new ImmunizationHistory();
		if (immunization.getVaccineCode().getCoding().size() > 0) {
			ecrImmunization.setCode(immunization.getVaccineCode().getCoding().get(0).getCode());
			ecrImmunization.setSystem(immunization.getVaccineCode().getCoding().get(0).getSystem());
            if(immunization.getVaccineCode().getText() != null){
                ecrImmunization.setCode(immunization.getVaccineCode().getText());
            }
		} 
        if(immunization.hasOccurrenceDateTimeType()){
            ecrImmunization.setDate(DateUtil.dateToStdString(immunization.getOccurrenceDateTimeType().getValue()));
        }
        else if(immunization.hasOccurrenceStringType()){
            ecrImmunization.setDate(immunization.getOccurrenceStringType().getValue());
        }
		if (!ecr.getPatient().getimmunizationHistory().contains(ecrImmunization)) {
			ecr.getPatient().getimmunizationHistory().add(ecrImmunization);
		}
        return ecr;
    }

    public ECR collectAndMapMedicationRequest(ECR ecr, String patientResourceId){

        List<MedicationRequest> medicationRequests = directFhirQueryService.medicationRequestSearch(patientResourceId);
        for(MedicationRequest mr:medicationRequests){
            mapMedicationRequest(ecr,mr);
        }
        return ecr;
    }

    public ECR mapMedicationRequest(ECR ecr, MedicationRequest medicationRequest){
        //Get the code from the MR or the referenced medication
        CodeableConcept ecrCode = new CodeableConcept();
		Medication ecrMedication = new Medication();
		Type medicationCodeUntyped = medicationRequest.getMedication();
		org.hl7.fhir.r4.model.CodeableConcept code = null;
        org.hl7.fhir.r4.model.Medication medication = null;
		if (medicationCodeUntyped instanceof org.hl7.fhir.r4.model.CodeableConcept) {
			code = (org.hl7.fhir.r4.model.CodeableConcept) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof Reference) {
			Reference medicationReference = (Reference) medicationCodeUntyped;
            String medicationId = medicationReference.getReferenceElement().getIdPart(); //TODO: Test that this is just the id
			medication = directFhirQueryService.medicationRead(medicationId);
			code = medication.getCode();
		}
        //Don't continue if the code is null
        if(code == null){
            return ecr;
        }
        ecrCode = findCCfromCode(code);
        //If we couldn't find a matching code it's not related to our search
        if(ecrCode == null){
            return ecr;
        }
        ecrMedication.setCode(ecrCode.getcode());
        ecrMedication.setSystem(ecrCode.getsystem());
        ecrMedication.setDisplay(ecrCode.getdisplay());
        if (!ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
            ecr.getPatient().getMedicationProvided().add(ecrMedication);
        }
        //Handle Dosage
        Dosage dosageInstruction = medicationRequest.getDosageInstructionFirstRep();
        gatech.edu.STIECR.JSON.Dosage ecrDosage = new gatech.edu.STIECR.JSON.Dosage();
        DosageDoseAndRateComponent doseUntyped = dosageInstruction.getDoseAndRateFirstRep();
        if(doseUntyped != null && doseUntyped.hasDoseQuantity()){
            SimpleQuantity doseTyped = (SimpleQuantity) doseUntyped.getDoseQuantity();
            ecrDosage.setValue(doseTyped.getValue().toString());
            ecrDosage.setUnit(doseTyped.getUnit());
        }
        else if(doseUntyped != null && doseUntyped.hasDoseRange()){
            Range dosageRange = (Range) doseUntyped.getDoseRange();
            BigDecimal high = dosageRange.getHigh().getValue();
            BigDecimal low = dosageRange.getLow().getValue();
            BigDecimal mean = high.add(low);
            mean = mean.divide(new BigDecimal(2));
            ecrDosage.setValue(mean.toString());
            ecrDosage.setUnit(dosageRange.getHigh().getUnit());
        }
        //TODO: Handle timing when timing is code
        if(dosageInstruction.getTiming() != null) {
            if(dosageInstruction.getTiming().getRepeat() != null) {
                TimingRepeatComponent repeat = dosageInstruction.getTiming().getRepeat();
                if(repeat.hasPeriod() && repeat.hasPeriodUnit() && repeat.hasFrequency()) {
                    String periodUnit = repeat.getPeriodUnit().getDisplay();
                    BigDecimal period = repeat.getPeriod();
                    Integer frequency = repeat.getFrequency();
                    String commonFrequency= "" + frequency
                            + " times per "
                            + period +
                            " " +periodUnit;
                    ecrMedication.setFrequency(commonFrequency);
                }
            }
        }
        ecrMedication.setDosage(ecrDosage);

        Date validityStart  = null;
        if(medicationRequest.getDispenseRequest() != null && medicationRequest.getDispenseRequest().getValidityPeriod() != null){
            validityStart = medicationRequest.getDispenseRequest().getValidityPeriod().getStart();
        }
        Date authoredOn = medicationRequest.getAuthoredOn();
        if(validityStart != null){
            ecrMedication.setDate(validityStart.toString());
        }
        else if(authoredOn != null) {
            ecrMedication.setDate(authoredOn.toString());
        }
        ecr.getPatient().getMedicationProvided().add(ecrMedication);
        return ecr;
    }

    //Utilities
    public CodeableConcept findCCfromCode(org.hl7.fhir.r4.model.CodeableConcept code){
        for(Coding coding:code.getCoding()){
            CodeableConcept cc = findCCfromCoding(coding);
            if(cc != null){
                return cc;
            }
        }
        //Don't return null but make a new codeableconcept from what's available
        CodeableConcept cc = new CodeableConcept();
        Coding firstCoding = code.getCodingFirstRep();
        if(firstCoding.hasCode()){
            cc.setcode(firstCoding.getCode());
        }
        if(firstCoding.hasSystem()){
            cc.setsystem(firstCoding.getSystem());
        }
        if(firstCoding.hasDisplay()){
            cc.setdisplay(firstCoding.getDisplay());
        }
        return cc;
    }

    public CodeableConcept findCCfromCoding(Coding fhirCoding){
        for(CodeableConcept cc:cqlConceptCaptureService.getAllCodes()){
            if(fhirCoding.getSystem().equalsIgnoreCase(cc.getsystem()) && fhirCoding.getCode().equalsIgnoreCase(cc.getcode())){
                return cc;
            }
        }
        for(CodeableConcept cc:cqlConceptCaptureService.getAllCodes()){
            if(fhirCoding.getCode().equalsIgnoreCase(cc.getcode())){
                return cc;
            }
        }
        return null;
    }

    public HumanName patientFindBestName(Patient patient){
        for(HumanName name:patient.getName()){
            if(name.getUse() != null && name.getUse().equals(NameUse.OFFICIAL)){
                return name;
            }
        }
        for(HumanName name:patient.getName()){
            if(name.getUse() != null && name.getUse().equals(NameUse.USUAL)){
                return name;
            }
        }
        return patient.getNameFirstRep();
    }

    public Address patientFindBestAddress(Patient patient){
        List<Address> addresses = patient.getAddress();
        if(addresses.size() == 0){
            return null;
        }
        for(Address address:addresses){
            if(address.getUse().equals(AddressUse.HOME)){
                return address;
            }
        }
        for(Address address:addresses){
            if(address.getUse().equals(AddressUse.BILLING)){
                return address;
            }
        }
        return addresses.get(0);
    }
}
