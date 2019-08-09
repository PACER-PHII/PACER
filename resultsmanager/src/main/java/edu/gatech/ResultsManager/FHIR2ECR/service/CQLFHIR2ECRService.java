package edu.gatech.ResultsManager.FHIR2ECR.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Age;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Dosage;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationDosageComponent;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.MedicationStatement;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.dstu3.model.Quantity;
import org.hl7.fhir.dstu3.model.Range;
import org.hl7.fhir.dstu3.model.Ratio;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.SampledData;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.TimeType;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.exceptions.FHIRException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import edu.gatech.ResultsManager.FHIR2ECR.util.HAPIFHIRUtil;
import edu.gatech.ResultsManager.fhirfilter.service.FHIRFilterService;
import gatech.edu.STIECR.JSON.Diagnosis;
import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.ImmunizationHistory;
import gatech.edu.STIECR.JSON.LabOrderCode;
import gatech.edu.STIECR.JSON.LabResult;
import gatech.edu.STIECR.JSON.Name;
import gatech.edu.STIECR.JSON.ParentGuardian;
import gatech.edu.STIECR.JSON.Provider;
import gatech.edu.STIECR.JSON.TypeableID;
import gatech.edu.STIECR.JSON.utils.DateUtil;
import gatech.edu.STIECR.controller.ControllerUtils;

@Service
public class CQLFHIR2ECRService {

	Logger log = LoggerFactory.getLogger(CQLFHIR2ECRService.class);
	FHIRFilterService fhirFilterService;
	Bundle globalBundle;
	IParser parser3;
	ObjectMapper objectMapper;
	
	public CQLFHIR2ECRService(FHIRFilterService fhirFilterService) {
		this.fhirFilterService = fhirFilterService;
		parser3 = FhirContext.forDstu3().newJsonParser();
		objectMapper = new ObjectMapper();
		globalBundle = new Bundle();
	}
	
	public ECR CQLFHIRResultsToECR(ArrayNode cqlResults) {
		ECR ecr = new ECR();
		initGlobalBundleFromResults(ecr,cqlResults);
		
		for(JsonNode result:cqlResults) {
			log.debug("Result:"+result.toString());
			if(result.get("resultType") != null) {
				String resultType = result.get("resultType").asText();
				String filteredResults = "";
				switch(resultType) {
				case "Patient":
					filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
					if(!filteredResults.equalsIgnoreCase("{}")) {
						Patient patient = (Patient)parser3.parseResource(filteredResults);
						handlePatient(ecr,patient);
						break;
					}
				case "FhirBundleCursorStu3":
				case "List":
					handleList(ecr,result.get("result").asText(),result.get("name").asText());
					break;
				case "Condition":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Condition condition = (Condition)parser3.parseResource(filteredResults);
						if(result.get("name").asText().equalsIgnoreCase("42.Condition.Diagnosis")) {
							log.debug("CONDITION --- Found diagnosis key!");
							addDiagnosis(ecr,condition);
						}
						handleCondition(ecr,condition);
						break;
					}
				case "MedicationAdministration":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationAdministration medicationAdministration = (MedicationAdministration)parser3.parseResource(filteredResults);
						handleMedicationAdministration(ecr,medicationAdministration);
						break;
					}
				case "MedicationRequest":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationRequest medicationRequest = (MedicationRequest)parser3.parseResource(filteredResults);
						handleMedicationRequest(ecr,medicationRequest);
						break;
					}
				case "MedicationStatement":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationStatement medicationStatement = (MedicationStatement)parser3.parseResource(filteredResults);
						handleMedicationStatement(ecr,medicationStatement);
						break;
					}
				case "Observation":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Observation observation = (Observation)parser3.parseResource(filteredResults);
						handleObservation(ecr,observation);
						break;
					}
				case "Procedure":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Procedure procedure = (Procedure)parser3.parseResource(filteredResults);
						handleProcedure(ecr,procedure);
						break;
					}
				case "String":
				case "StringType":
					addStringResultByResultKey(ecr,result);
					break;
				case "DateTimeType":
				case "DateTime":
					addDateTimeResultByResultKey(ecr,result);
					break;
				}
			}
		}
		return ecr;
	}
	
	public void initGlobalBundleFromResults(ECR ecr,ArrayNode cqlResults) {
		globalBundle = new Bundle();
		for(JsonNode result:cqlResults) {
			log.debug("Result:"+result.toString());
			if(result.get("resultType") != null) {
				String resultType = result.get("resultType").asText();
				String filteredResults = "";
				switch(resultType) {
				case "Patient":
				case "Condition":
				case "MedicationAdministration":
				case "MedicationRequest":
				case "MedicationStatement":
				case "Observation":
				case "Procedure":
					filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
					
					if(!filteredResults.equalsIgnoreCase("{}")) {
						Resource resource = (Resource)parser3.parseResource(filteredResults);
						globalBundle.addEntry(new BundleEntryComponent().setResource(resource));
						break;
					}
				case "FhirBundleCursorStu3":
				case "List":
					String listString = result.get("result").asText();
					log.debug("HANDLE LIST --- inputString:"+listString);
					
					//Check for json list. Handling only json resources.
					if(listString.substring(0, 2).equalsIgnoreCase("[{")) {
						ArrayNode arrayNode = null;
						try {
							arrayNode = (ArrayNode)objectMapper.readTree(listString);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for(JsonNode node : arrayNode) {
							log.debug("HANDLE LIST --- node:"+node.toString());
							String filteredResource = fhirFilterService.applyFilter(node,true);
							IBaseResource resource = parser3.parseResource(filteredResource);
							globalBundle.addEntry(new BundleEntryComponent().setResource((Resource)resource));
						}
					}
					/*handleList(ecr,result.get("result").asText(),result.get("name").asText());*/
					break;
				case "String":
				case "StringType":
					addStringResultByResultKey(ecr,result);
					break;
				case "DateTimeType":
				case "DateTime":
					addDateTimeResultByResultKey(ecr,result);
					break;
				}
			}
		}
	}
	
	void handleList(ECR ecr,String list,String keyName) {
		log.debug("HANDLE LIST --- inputString:"+list);
		//Check for json list. Handling only json resources.
		if(list.substring(0, 2).equalsIgnoreCase("[{")) {
			ArrayNode arrayNode = null;
			try {
				arrayNode = (ArrayNode)objectMapper.readTree(list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bundle inputBundle = new Bundle();
			for(JsonNode node : arrayNode) {
				log.debug("HANDLE LIST --- node:"+node.toString());
				String filteredResource = fhirFilterService.applyFilter(node,true);
				IBaseResource resource = parser3.parseResource(filteredResource);
				inputBundle.addEntry(new BundleEntryComponent().setResource((Resource)resource));
			}
			handleBundle(ecr,inputBundle,keyName);
		}
	}
	
	void handleBundle(ECR ecr, Bundle bundle,String keyName) {
		for(BundleEntryComponent entry : bundle.getEntry()) {
			Resource resource = entry.getResource();
			if(resource != null) {
				if(resource instanceof Patient) {
					handlePatient(ecr,(Patient)resource);
				}
				if(resource instanceof RelatedPerson) {
					handleRelatedPerson(ecr,(RelatedPerson)resource);
				}
				if(resource instanceof Practitioner) {
					handlePractitioner(ecr,(Practitioner)resource);
				}
				if(resource instanceof MedicationAdministration) {
					handleMedicationAdministration(ecr,(MedicationAdministration)resource);
				}
				if(resource instanceof MedicationRequest) {
					handleMedicationRequest(ecr,(MedicationRequest)resource);
				}
				if(resource instanceof MedicationStatement) {
					handleMedicationStatement(ecr,(MedicationStatement)resource);
				}
				if(resource instanceof Condition) {
					if(keyName.equalsIgnoreCase("42.Condition.Diagnosis")) {
						log.debug("CONDITION --- Found diagnosis key!");
						addDiagnosis(ecr,(Condition)resource);
					}
					handleCondition(ecr,(Condition)resource);
				}
				if(resource instanceof Encounter) {
					handleEncounter(ecr,(Encounter)resource);
				}
				if(resource instanceof Observation) {
					handleObservation(ecr,(Observation)resource);
				}
				if(resource instanceof Procedure) {
					handleProcedure(ecr,(Procedure)resource);
				}
			}
		}
		updateDateOfOnset(ecr);
	}
	
	protected void updateDateOfOnset(ECR ecr) {
		if (StringUtils.isBlank(ecr.getPatient().getdateOfOnset())) {
			for (LabOrderCode labcode : ecr.getPatient().getlabOrderCode()) {
				for (LabResult labresult : labcode.getLaboratory_Results()) {
					if (labresult.getValue().toLowerCase().contains("positive")) {
						if (!StringUtils.isBlank(labresult.getDate())) {
							log.info("LabResult --- Found onset date of: " + labresult.getDate());
							ecr.getPatient().setdateOfOnset(labresult.getDate());
							break;
						}
					}
				}
			}
		}
	}

	void handlePatient(ECR ecr, org.hl7.fhir.dstu3.model.Patient patient) {
		log.info("PATIENT --- ");
		if(patient.getBirthDate() != null) {
			ecr.getPatient().setbirthDate(patient.getBirthDate().toString());
		}
		Type deceasedValue = patient.getDeceased();
		if (deceasedValue != null && deceasedValue instanceof DateTimeType) {
			log.debug("*~*Andrey*~* Setting deathDate from handlePatient");
			String deathDate = DateUtil.dateToStdString(((DateTimeType) deceasedValue).getValue());
			log.debug("*~*Andrey*~* from patient.deceased value:"+deathDate);
			ecr.getPatient().setdeathDate(deathDate);
		}
		for(Extension extension:patient.getExtension()) {
			if(extension.getUrlElement().equals("http://hl7.org/fhir/us/core/StructureDefinition/us-core-race")) {
				Coding coding = (Coding)extension.getExtensionFirstRep().getValue();
				ecr.getPatient().setrace(new gatech.edu.STIECR.JSON.CodeableConcept(coding.getSystem(),coding.getDisplay(),coding.getCode()));
			}
			if(extension.getUrlElement().equals("http://hl7.org/fhir/us/core/StructureDefinition/us-core-ethnicity")) {
				Coding coding = (Coding)extension.getExtensionFirstRep().getValue();
				ecr.getPatient().setethnicity(new gatech.edu.STIECR.JSON.CodeableConcept(coding.getSystem(),coding.getDisplay(),coding.getCode()));
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
			Address address = patient.getAddressFirstRep();
			String addressString = HAPIFHIRUtil.addressToString(address);
			ecr.getPatient().setstreetAddress(addressString);
		}
		if(patient.getGender() != null) {
			ecr.getPatient().setsex(patient.getGender().toCode());
		}
	}

	void handleRelatedPerson(ECR ecr, RelatedPerson relatedPerson) {
		log.info("RELATED PERSON --- ");
		Name nameToSearch = new Name(relatedPerson.getName().get(0).getFamily(),
				relatedPerson.getName().get(0).getGiven().get(0).getValueNotNull());
		ParentGuardian ecrParentGuardian = ecr.findParentGuardianWithName(nameToSearch);
		if (ecrParentGuardian == null) {
			ecrParentGuardian = new ParentGuardian();
			ecrParentGuardian.setname(nameToSearch);
			updateParentGuardian(ecrParentGuardian, relatedPerson);
			ecr.getPatient().getparentsGuardians().add(ecrParentGuardian);
		} else {
			updateParentGuardian(ecrParentGuardian, relatedPerson);
		}
	}

	void handlePractitioner(ECR ecr, Practitioner provider) {
		log.info("PRACTITIONER --- ");
		Provider ecrProvider = new Provider();
		ecrProvider.setaddress(provider.getAddress().get(0).getText());
		ecrProvider.setcountry(provider.getAddress().get(0).getCountry());
		for (ContactPoint contact : provider.getTelecom()) {
			if (contact.getSystem().equals("Phone") && ecrProvider.getphone().isEmpty()) {
				ecrProvider.setphone(contact.getValue());
			} else if (contact.getSystem().equals("Email") && ecrProvider.getemail().isEmpty()) {
				ecrProvider.setemail(contact.getValue());
			}
		}
		// Update or add to the current provider list
		if (ecr.getProvider().contains(ecrProvider)) {
			for (Provider originalProvider : ecr.getProvider()) {
				if (originalProvider.equals(ecrProvider))
					originalProvider.update(ecrProvider);
			}
		} else {
			ecr.getProvider().add(ecrProvider);
		}
	}

	void handleMedicationAdministration(ECR ecr, MedicationAdministration medicationAdministration ) {
		gatech.edu.STIECR.JSON.CodeableConcept ecrCode = new gatech.edu.STIECR.JSON.CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONADMINISTRATION --- Trying medicationAdministration: "
				+ medicationAdministration.getId());
		Type medicationCodeUntyped = medicationAdministration.getMedication();
		log.info("MEDICATIONADMINISTRATION --- medication code element class: "
				+ medicationCodeUntyped.getClass());
		
		CodeableConcept code  = null;
		
		if (medicationCodeUntyped instanceof CodeableConcept) {
			code = (CodeableConcept) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof Reference) {
			Reference reference = (Reference) medicationCodeUntyped;
			Medication medication = (Medication)findResourceFromReferenceInGlobalBundle(reference);
			code = medication.getCode();
		}
		
		log.info("MEDICATIONADMINISTRATION --- Trying code with this many codings: "
				+ code.getCoding().size());
		if(code != null) {
			for (Coding coding : code.getCoding()) {
				log.info("MEDICATIONADMINISTRATION --- Trying coding: " + coding.getDisplay());
				gatech.edu.STIECR.JSON.CodeableConcept ecrConcept = FHIRCoding2ECRConcept(coding);
				log.info("MEDICATIONADMINISTRATION --- Translated to ECRconcept:" + ecrConcept.toString());
				ecrMedication.setCode(ecrConcept.getcode());
				ecrMedication.setSystem(ecrConcept.getsystem());
				ecrMedication.setDisplay(ecrConcept.getdisplay());
				ecrCode.setcode(ecrConcept.getcode());
				ecrCode.setsystem(ecrConcept.getsystem());
				ecrCode.setdisplay(ecrConcept.getdisplay());
			}
		}
		MedicationAdministrationDosageComponent dosage = medicationAdministration.getDosage();
		if (!dosage.isEmpty()) {
			gatech.edu.STIECR.JSON.Dosage ecrDosage = new gatech.edu.STIECR.JSON.Dosage();
			if(dosage.hasDose()) {
				ecrDosage.setValue(dosage.getDose().getValue().toPlainString());
				ecrDosage.setUnit(dosage.getDose().getUnit());
			}
			ecrMedication.setDosage(ecrDosage);
		}
		try
		{
			if (medicationAdministration.getEffective() instanceof DateTimeType) {
				ecrMedication.setDate(HAPIFHIRUtil.getDate(medicationAdministration.getEffectiveDateTimeType()).toString());
			}
			else if (medicationAdministration.getEffective() instanceof Period) {
				ecrMedication.setDate(HAPIFHIRUtil.getDate(medicationAdministration.getEffectivePeriod().getStartElement()).toString());
			}
		}
		catch(FHIRException e) {
			log.error(e.getMessage());
			log.error("Typing error non-halting. Continuing.");
		}
		log.info("MEDICATIONADMINISTRATION --- ECRCode: " + ecrCode);
		if (ControllerUtils.isSTIMed(ecrCode)
				&& !ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
			log.info("MEDICATIONADMINISTRATION --- Found New Entry: " + ecrCode);
			ecr.getPatient().getMedicationProvided().add(ecrMedication);
		} else {
			log.info("MEDICATIONADMINISTRATION --- Didn't Match or found duplicate! " + ecrCode);
		}

		if (!medicationAdministration.getNotGiven()) {
			for (CodeableConcept reason : medicationAdministration.getReasonCode()) {
				handleConditionConceptCode(ecr, reason);
			}
		}
		
	}

	void handleMedicationRequest(ECR ecr, MedicationRequest medicationRequest) {
		gatech.edu.STIECR.JSON.CodeableConcept ecrCode = new gatech.edu.STIECR.JSON.CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONREQUEST --- Trying medicationOrder: " + medicationRequest.getId());
		Type medicationCodeUntyped = medicationRequest.getMedication();
		log.info("MEDICATIONREQUEST --- medication code element class: " + medicationCodeUntyped.getClass());

		CodeableConcept code = null;

		if (medicationCodeUntyped instanceof CodeableConcept) {
			code = (CodeableConcept) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof Reference) {
			Reference reference = (Reference) medicationCodeUntyped;
			Medication medication = (Medication)findResourceFromReferenceInGlobalBundle(reference);
			code = medication.getCode();
		}
		if (code != null && !code.getCoding().isEmpty()) {
			log.info("MEDICATIONREQUEST --- Trying coding: " + code.getCodingFirstRep().getDisplay());
			gatech.edu.STIECR.JSON.CodeableConcept concept = FHIRCoding2ECRConcept(code.getCodingFirstRep());
			log.info("MEDICATIONREQUEST --- Translated to ECRconcept:" + concept.toString());
			ecrMedication.setCode(concept.getcode());
			ecrMedication.setSystem(concept.getsystem());
			ecrMedication.setDisplay(concept.getdisplay());
			ecrCode.setcode(concept.getcode());
			ecrCode.setsystem(concept.getsystem());
			ecrCode.setdisplay(concept.getdisplay());
			log.info("MEDICATIONREQUEST --- ECRCode: " + ecrCode);
			if (!ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
				log.info("MEDICATIONREQUEST --- Found New Entry: " + ecrCode);
				ecr.getPatient().getMedicationProvided().add(ecrMedication);
			} else {
				log.info("MEDICATIONREQUEST --- Didn't Match or found duplicate! " + ecrCode);
			}
			for (Dosage dosageInstruction : medicationRequest.getDosageInstruction()) {
				gatech.edu.STIECR.JSON.Dosage ecrDosage = new gatech.edu.STIECR.JSON.Dosage();
				Type doseUntyped = dosageInstruction.getDose();
				if (doseUntyped != null) {
					log.info("MEDICATIONREQUEST --- Found Dosage: " + doseUntyped.toString());
					if (doseUntyped instanceof SimpleQuantity) {
						SimpleQuantity doseTyped = (SimpleQuantity) doseUntyped;
						log.info("MEDICATIONREQUEST --- Dosage is of SimpleQuentity Type");
						ecrDosage.setValue(doseTyped.getValue().toString());
						ecrDosage.setUnit(doseTyped.getUnit());
					}
					else if (doseUntyped instanceof Range) {
						Range dosageRange = (Range) doseUntyped;
						BigDecimal high = dosageRange.getHigh().getValue();
						BigDecimal low = dosageRange.getLow().getValue();
						BigDecimal mean = high.add(low);
						mean = mean.divide(new BigDecimal(2));
						ecrDosage.setValue(mean.toString());
						ecrDosage.setUnit(dosageRange.getHigh().getUnit());
					}
					if(dosageInstruction.getTiming() != null) {
						String periodUnit = dosageInstruction.getTiming().getRepeat().getPeriodUnit().getDisplay();
						BigDecimal period = dosageInstruction.getTiming().getRepeat().getPeriod();
						Integer frequency = dosageInstruction.getTiming().getRepeat().getFrequency();
						String commonFrequency= "" + frequency
								+ " times per "
								+ period +
								" " +periodUnit;
						// log.info("MEDICATIONREQUEST --- Found Frequency: " + commonFrequency);
						ecrMedication.setFrequency(commonFrequency);
					}
					ecrMedication.setDosage(ecrDosage);
				}
				else {
					log.info("MEDICATIONREQUEST --- DOSE NOT FOUND.");
				}
			}

			Period period = medicationRequest.getDispenseRequest().getValidityPeriod();
			if (period != null && period.getStart() != null) {
				log.info("MEDICATIONREQUEST --- Found Validity Period: " + period.getStart().toLocaleString());
				ecrMedication.setDate(period.getStart().toString());
			}
			if (medicationRequest.getReasonCode() != null && !medicationRequest.getReasonCode().isEmpty()) {
				handleConditionConceptCode(ecr, medicationRequest.getReasonCodeFirstRep());
			}
		}
	}

	void handleMedicationStatement(ECR ecr, MedicationStatement medicationStatement) {
		gatech.edu.STIECR.JSON.CodeableConcept ecrCode = new gatech.edu.STIECR.JSON.CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONSTATEMENT --- Trying medicationStatement: " + medicationStatement.getId());
		Type medicationCodeUntyped = medicationStatement.getMedication();
		log.info("MEDICATIONSTATEMENT --- medication code element class: " + medicationCodeUntyped.getClass());

		CodeableConcept code = null;

		if (medicationCodeUntyped instanceof CodeableConcept) {
			code = (CodeableConcept) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof Reference) {
			code = ((Medication) ((Reference) medicationCodeUntyped).getResource()).getCode();
		}
		if (code != null && !code.getCoding().isEmpty()) {
			log.info("MEDICATIONSTATEMENT --- Trying coding: " + code.getCodingFirstRep().getDisplay());
			gatech.edu.STIECR.JSON.CodeableConcept concept = FHIRCoding2ECRConcept(code.getCodingFirstRep());
			log.info("MEDICATIONSTATEMENT --- Translated to ECRconcept:" + concept.toString());
			ecrMedication.setCode(concept.getcode());
			ecrMedication.setSystem(concept.getsystem());
			ecrMedication.setDisplay(concept.getdisplay());
			ecrCode.setcode(concept.getcode());
			ecrCode.setsystem(concept.getsystem());
			ecrCode.setdisplay(concept.getdisplay());
			log.info("MEDICATIONSTATEMENT --- ECRCode: " + ecrCode);
			if (!ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
				log.info("MEDICATIONSTATEMENT --- Found New Entry: " + ecrCode);
				ecr.getPatient().getMedicationProvided().add(ecrMedication);
			} else {
				log.info("MEDICATIONSTATEMENT --- Didn't Match or found duplicate! " + ecrCode);
			}
			for (Dosage dosageInstruction : medicationStatement.getDosage()) {
				gatech.edu.STIECR.JSON.Dosage ecrDosage = new gatech.edu.STIECR.JSON.Dosage();
				Type doseUntyped = dosageInstruction.getDose();
				if (doseUntyped != null) {
					log.info("MEDICATIONSTATEMENT --- Found Dosage: " + doseUntyped.toString());
					if (doseUntyped instanceof SimpleQuantity) {
						SimpleQuantity doseTyped = (SimpleQuantity) doseUntyped;
						log.info("MEDICATIONSTATEMENT --- Dosage is of SimpleQuentity Type");
						ecrDosage.setValue(doseTyped.getValue().toString());
						ecrDosage.setUnit(doseTyped.getUnit());
					}
					else if (doseUntyped instanceof Range) {
						Range dosageRange = (Range) doseUntyped;
						BigDecimal high = dosageRange.getHigh().getValue();
						BigDecimal low = dosageRange.getLow().getValue();
						BigDecimal mean = high.add(low);
						mean = mean.divide(new BigDecimal(2));
						ecrDosage.setValue(mean.toString());
						ecrDosage.setUnit(dosageRange.getHigh().getUnit());
					}
					if(dosageInstruction.getTiming() != null) {
						String periodUnit = dosageInstruction.getTiming().getRepeat().getPeriodUnit().getDisplay();
						BigDecimal period = dosageInstruction.getTiming().getRepeat().getPeriod();
						Integer frequency = dosageInstruction.getTiming().getRepeat().getFrequency();
						String commonFrequency= "" + frequency
								+ " times per "
								+ period +
								" " +periodUnit;
						// log.info("MEDICATIONSTATEMENT --- Found Frequency: " + commonFrequency);
						ecrMedication.setFrequency(commonFrequency);
					}
					ecrMedication.setDosage(ecrDosage);
				}
				else {
					log.info("MEDICATIONSTATEMENT --- DOSE NOT FOUND.");
				}
			}
			if (!medicationStatement.getDateAssertedElement().isEmpty()) {
				if (medicationStatement.getDateAsserted() != null) {
					String dateTimeAsString = DateUtil.dateTimeToStdString(medicationStatement.getDateAsserted());
					log.info("MEDICATIONSTATEMENT  --- Found Medication Date: " + dateTimeAsString);
					ecrMedication.setDate(dateTimeAsString);
				}
				log.info("MEDICATIONSTATEMENT  --- ECRCode: " + ecrCode);
			}
			if (medicationStatement.getReasonCode() != null && !medicationStatement.getReasonCode().isEmpty()) {
				handleConditionConceptCode(ecr, medicationStatement.getReasonCodeFirstRep());
			}
			
			if (!ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
				log.info("MEDICATIONSTATEMENT  --- Found New Entry: " + ecrCode);
				ecr.getPatient().getMedicationProvided().add(ecrMedication);
			} else {
				log.info("MEDICATIONSTATEMENT  --- Didn't Match or found duplicate! " + ecrCode);
			}
		}
		if (medicationStatement.getReasonCode() != null && !medicationStatement.getReasonCode().isEmpty()) {
			handleConditionConceptCode(ecr,medicationStatement.getReasonCodeFirstRep());
		}
	}

	void handleImmunization(ECR ecr, Immunization immunization) {
		log.info("IMMUNIZATION --- ");
		ImmunizationHistory ecrImmunization = new ImmunizationHistory();
		if (immunization != null && immunization.getVaccineCode().getCoding().size() > 0) {
			ecrImmunization.setCode(immunization.getVaccineCode().getCoding().get(0).getCode());
			ecrImmunization.setSystem(immunization.getVaccineCode().getCoding().get(0).getSystem());
		} else
		if (immunization != null && StringUtils.isNotBlank(immunization.getVaccineCode().getText())) {
			ecrImmunization.setCode(immunization.getVaccineCode().getText());
		} else
		if (immunization != null && StringUtils.isNotBlank(immunization.getText().getDivAsString())) {
			ecrImmunization.setCode(immunization.getText().getDivAsString());
		}
		ecrImmunization.setDate(DateUtil.dateToStdString(immunization.getDate()));
		if (!ecr.getPatient().getimmunizationHistory().contains(ecrImmunization)) {
			log.info("Adding Immunization For " + immunization.getId());
			ecr.getPatient().getimmunizationHistory().add(ecrImmunization);
		}
	}

	public void handleConditionConceptCode(ECR ecr, CodeableConcept code) {
		log.info("CONDITION --- Trying code with this many codings: " + code.getCoding().size());
		for (Coding coding : code.getCoding()) {
			log.info("CONDITION --- Trying coding: " + coding.getDisplay());
			gatech.edu.STIECR.JSON.CodeableConcept concept = FHIRCoding2ECRConcept(coding);
			log.info("CONDITION --- Translated to ECRconcept:" + concept.toString());
			if (!ecr.getPatient().getsymptoms().contains(concept)) {
				log.info("CONDITION --- SYMPTOM MATCH!" + concept.toString());
				ecr.getPatient().getsymptoms().add(concept);
				break; // Stop once we get a codingDt that matches our list of codes.
			}
		}
	}

	public void handleCondition(ECR ecr, Condition condition) {
		log.info("CONDITION --- Trying condition: " + condition.getId());
	
		Date onsetDate = null;
		if (condition.getOnset() != null) {
			Type onsetUntyped = condition.getOnset();
			if(onsetUntyped instanceof Age) {
				onsetDate = HAPIFHIRUtil.getDate((Age) onsetUntyped);
			}
			if(onsetUntyped instanceof DateTimeType) {
				onsetDate = HAPIFHIRUtil.getDate((DateTimeType) onsetUntyped);
			}
			if(onsetUntyped instanceof Period) {
				onsetDate = HAPIFHIRUtil.getDate((Period) onsetUntyped);
			}
		}
		Date ecrDate = null;
		try {
			String onsetDateStr = ecr.getPatient().getdateOfOnset();
			ecrDate = DateUtil.parse(onsetDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		CodeableConcept code = condition.getCode();
		log.info("CONDITION --- Trying code with this many codings: " + code.getCoding().size());
		for (Coding coding : code.getCoding()) {
			log.info("CONDITION --- Trying coding: " + coding.getDisplay());
			gatech.edu.STIECR.JSON.CodeableConcept concept = FHIRCoding2ECRConcept(coding);
			log.info("CONDITION --- Translated to ECRconcept:" + concept.toString());
			if (diagnosisContainsCodeableConcept(ecr.getPatient().getDiagnosis(),concept)) {
				log.info("CONDITION ---DIAGNOSIS MATCH!" + concept.toString());
				Diagnosis updatedDiagnosis = new Diagnosis();
				updatedDiagnosis.setCode(concept.getcode());
				updatedDiagnosis.setDisplay(concept.getdisplay());
				updatedDiagnosis.setSystem(concept.getsystem());
				if ((ecrDate == null && onsetDate != null)
						|| (ecrDate != null && onsetDate != null && onsetDate.before(ecrDate))) {
					log.info("CONDITION --- Found onset date of: " + onsetDate);
					log.info("CONDITION --- Eariler date than previously found. Replacing patient onset date.");
					ecr.getPatient().setdateOfOnset(DateUtil.dateTimeToStdString(onsetDate));
					updatedDiagnosis.setDate(DateUtil.dateTimeToStdString(onsetDate));
				} else {
					updatedDiagnosis.setDate(ecr.getPatient().getdateOfOnset());
				}
				ecr.getPatient().getDiagnosis().add(updatedDiagnosis);
				return;
			}
		}
		handleConditionConceptCode(ecr, code);
		// TODO: distinguish between symptom list and diagnosis list here
		// TODO: Map Pregnant from encounters
	}

	void handleEncounter(ECR ecr, Encounter encounter) {
		log.info("ENCOUNTER --- Trying encounter: " + encounter.getId());
		for (CodeableConcept reason : encounter.getReason()) {
			if(!reason.getCoding().isEmpty()) {
				gatech.edu.STIECR.JSON.CodeableConcept concept = FHIRCoding2ECRConcept(reason.getCodingFirstRep());
				if (!ecr.getPatient().getsymptoms().contains(concept)) {
					ecr.getPatient()
							.setvisitDateTime(DateUtil.dateTimeToStdString(encounter.getPeriod().getStart()));
				}
			}
		}
	}

	void handleObservation(ECR ecr, Observation observation) {
		log.info("OBSERVATION --- Trying observation: " + observation.getId());
		CodeableConcept code = observation.getCode();
		for (Coding coding : code.getCoding()) {
			if (coding.getCode().equalsIgnoreCase("laboratory")) { // HIT! Found a lab result
				LabOrderCode labOrder = new LabOrderCode();
				labOrder.setcode(coding.getCode());
				labOrder.setdisplay(coding.getDisplay());
				labOrder.setsystem(coding.getSystem());
				LabResult labResult = new LabResult();
				Type untypedValue = observation.getValue();
				if (untypedValue instanceof Quantity) {
					labResult.setValue(((Quantity) untypedValue).getValue().toString());
				} else
				if (untypedValue instanceof CodeableConcept && !((CodeableConcept) untypedValue).getCoding().isEmpty()) {
					labResult.setValue(((CodeableConcept) untypedValue).getCodingFirstRep().getDisplay());
				} else
				if (untypedValue instanceof StringType) {
					labResult.setValue(((StringType) untypedValue).toString());
				} else
				if (untypedValue instanceof Range) {
					Range range = (Range) untypedValue;
					labResult.setValue("High:" + range.getHigh() + ";low:" + range.getLow());
				} else
				if (untypedValue instanceof Ratio) {
					Ratio ratio = (Ratio) untypedValue;
					labResult.setValue(
							ratio.getNumerator().toString() + "/" + ratio.getDenominator().toString());
				} else
				if (untypedValue instanceof SampledData) {
					labResult.setValue(((SampledData) untypedValue).getData());
				} else
				if (untypedValue instanceof TimeType) {
					labResult.setValue(((TimeType) untypedValue).getValue());
				} else
				if (untypedValue instanceof DateTimeType) {
					labResult.setValue(((DateTimeType) untypedValue).getValueAsString());
				} else
				if (untypedValue instanceof Period) {
					Period period = (Period) untypedValue;
					labResult.setValue(
							"Start:" + period.getStart().toString() + ";End" + period.getEnd().toString());
				}
				labOrder.getLaboratory_Results().add(labResult);
			}
		}
	}

	void handleProcedure(ECR ecr,Procedure procedure) {
		log.info("PROCEDURE --- Trying procedure: " + procedure.getId());
		if (procedure.getReasonCode() != null) {
			handleConditionConceptCode(ecr,procedure.getReasonCodeFirstRep());
		}
	}

	void updateParentGuardian(ParentGuardian pg, RelatedPerson rp) {
		for (ContactPoint contact : rp.getTelecom()) {
			if (contact.getSystem().equals("Phone") && pg.getphone().isEmpty()) {
				pg.setphone(contact.getValue());
			} else
			if (contact.getSystem().equals("Email") && pg.getemail().isEmpty()) {
				pg.setemail(contact.getValue());
			}
		}
	}

	public static gatech.edu.STIECR.JSON.CodeableConcept FHIRCoding2ECRConcept(Coding fhirCoding) {
		gatech.edu.STIECR.JSON.CodeableConcept ecrConcept = new gatech.edu.STIECR.JSON.CodeableConcept();
		ecrConcept.setcode(fhirCoding.getCode());
		ecrConcept.setsystem(fhirCoding.getSystem());
		if (fhirCoding.getSystem().equals("http://snomed.info/sct")) {
			ecrConcept.setsystem("SNOMED CT");
		} else
		if (fhirCoding.getSystem().equals("http://www.nlm.nih.gov/research/umls/rxnorm")) {
			ecrConcept.setsystem("RxNorm");
		}
		ecrConcept.setdisplay(fhirCoding.getDisplay());
		return ecrConcept;
	}
	
	public void addStringResultByResultKey(ECR ecr,JsonNode result) {
		log.debug("STRING --- handling string:"+result);
		if(result.get("resultType").toString().equalsIgnoreCase("Null"))
			return;
		String value = result.get("result").asText();
		String key = result.get("name").asText();
		log.debug("STRING --- trying key:"+key);
		switch(key) {
		case "19.Patient.ID":
			TypeableID typeId = new TypeableID();
			typeId.settype("fhir");
			typeId.setvalue(value);
			ecr.getPatient().getid().add(typeId);
			break;
		case "20A.Patient.Name.given":
			ecr.getPatient().getname().setgiven(value);
			break;
		case "20B.Patient.Name.family":
			ecr.getPatient().getname().setfamily(value);
			break;
		case "24.Patient.Street_Address":
			ecr.getPatient().setstreetAddress(value);
			break;
		case "25.Patient.Birth_Date":
			ecr.getPatient().setbirthDate(value);
			break;
		case "26.Patient.Sex":
			ecr.getPatient().setsex(value);
			break;
		case "27.Patient.Class":
			ecr.getPatient().setpatientClass(value);
			break;
		case "28.Patient.Race":
			ecr.getPatient().setrace(new gatech.edu.STIECR.JSON.CodeableConcept("","",value));
			break;
		case "29.Patient.Ethnicity":
			ecr.getPatient().setethnicity(new gatech.edu.STIECR.JSON.CodeableConcept("","",value));
			break;
		case "30.Patient.Preferred_Language":
			ecr.getPatient().setpreferredLanguage(new gatech.edu.STIECR.JSON.CodeableConcept("","",value));
			break;
		case "31.Patient.Occupation":
			ecr.getPatient().setoccupation(value);
		case "32.Patient.Pregnancy":
			ecr.getPatient().setpregnant(value.equalsIgnoreCase("false") ? false : true);
			break;
		case "33.Patient.Travel_History":
			ecr.getPatient().gettravelHistory().add(value);
			break;
		default:
			log.debug("STRING --- Didn't match to any key!");
			break;
		}
	}
	
	public void addDateTimeResultByResultKey(ECR ecr,JsonNode result) {
		Date value = null;
		try {
			value = HAPIFHIRUtil.getDateFromCQLDateTimeString(result.get("result").asText());
		} catch (ParseException e) {
			log.error("Couldn't format datetime:"+result.get("result").asText());
			return;
		}
		String key = result.get("name").asText();
		switch(key) {
			case "43.Encounters.Date_Of_Diagnosis":
				ecr.getPatient().setdateOfOnset(value.toString());
				break;
			case "45.Patient.Death_Date":
				log.debug("*~*Andrey*~* Setting deathDate from 45.Patient.Death_Date cql key");
				log.debug("*~*Andrey*~* deathDate value:"+value.toString());
				ecr.getPatient().setdeathDate(value.toString());
				break;
			case "46.Patient.Date_Discharged":
				ecr.getPatient().setdateDischarged(value.toString());
				break;
		}
	}
	
	public void addDiagnosis(ECR ecr, Condition condition) {
		Diagnosis diagnosis = new Diagnosis();
		if(condition.getCode() != null && !condition.getCode().getCoding().isEmpty()) {
			Coding coding = condition.getCode().getCodingFirstRep();
			diagnosis.setCode(coding.getCode());
			diagnosis.setDisplay(coding.getDisplay());
			diagnosis.setSystem(coding.getSystem());
		}
		if(condition.getOnset() != null) {
			try {
				diagnosis.setDate(HAPIFHIRUtil.getDate(condition.getOnsetDateTimeType()).toString());
			} catch (FHIRException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(condition.getAssertedDate() != null) {
			diagnosis.setDate(condition.getAssertedDate().toString());
		}
		if(!ecr.getPatient().getDiagnosis().contains(diagnosis)) {
			ecr.getPatient().getDiagnosis().add(diagnosis);
		}
	}
	
	public static boolean diagnosisContainsCodeableConcept(List<Diagnosis> listDiagnosis,gatech.edu.STIECR.JSON.CodeableConcept ecrConcept) {
		for(Diagnosis singleDiagnosis:listDiagnosis) {
			if(singleDiagnosis.getCode().equals(ecrConcept.getcode())
					&& singleDiagnosis.getSystem().equals(ecrConcept.getsystem())) {
				return true;
			}
		}
		return false;
	}

	public FHIRFilterService getFhirFilterService() {
		return fhirFilterService;
	}

	public void setFhirFilterService(FHIRFilterService fhirFilterService) {
		this.fhirFilterService = fhirFilterService;
	}
	
	private Resource findResourceFromReferenceInGlobalBundle(Reference reference) {
		String referenceString = reference.getReference();
		String referenceId = referenceString.indexOf('/') == -1 ? referenceString : referenceString.substring(referenceString.charAt('/'));
		for(BundleEntryComponent entry:globalBundle.getEntry()) {
			Resource resource = entry.getResource();
			if(resource != null && resource.getId().equalsIgnoreCase(referenceId))
				return resource;
		}
		return null;
	}
	
	private Resource followReference(Bundle bundle, DomainResource resource, ResourceType resourceType, Reference reference){
		IIdType id = reference.getReferenceElement();
		if(id.isLocal()){
			for(Resource containedResource: resource.getContained()) {
				if(referenceMatchesResource(resource,reference)){
					return containedResource;
				}
			}
		}
		else {
			for(BundleEntryComponent bec:bundle.getEntry()) {
				Resource bundleResource = bec.getResource();
				if(bundleResource != null && referenceMatchesResource(bundleResource,reference)) {
					return bundleResource;
				}
			}
		}
		
		return null;
	}
	
	private boolean referenceMatchesResource(Resource resource,Reference reference) {
		IIdType id = reference.getReferenceElement();
		return resource.getIdElement().getIdPart().equals(id.getIdPart());
	}
}