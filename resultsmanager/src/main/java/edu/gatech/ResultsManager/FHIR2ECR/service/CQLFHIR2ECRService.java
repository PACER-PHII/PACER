package edu.gatech.ResultsManager.FHIR2ECR.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.google.gson.JsonObject;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.dstu2.composite.RatioDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.composite.SampledDataDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Bundle.Entry;
import ca.uhn.fhir.model.dstu2.resource.Claim;
import ca.uhn.fhir.model.dstu2.resource.Condition;
import ca.uhn.fhir.model.dstu2.resource.Conformance.RestResource;
import ca.uhn.fhir.model.dstu2.resource.Conformance.RestResourceInteraction;
import ca.uhn.fhir.model.dstu2.resource.Coverage;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Immunization;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationDispense;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;
import ca.uhn.fhir.model.dstu2.resource.MedicationStatement;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Patient.Link;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.resource.RelatedPerson;
import ca.uhn.fhir.model.dstu2.valueset.TypeRestfulInteractionEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.model.primitive.TimeDt;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import edu.gatech.ResultsManager.FHIR2ECR.util.HAPIFHIRUtil;
import edu.gatech.ResultsManager.fhirfilter.service.FHIRFilterService;
import gatech.edu.STIECR.JSON.CodeableConcept;
import gatech.edu.STIECR.JSON.Diagnosis;
import gatech.edu.STIECR.JSON.Dosage;
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
	IParser parser2;
	ObjectMapper objectMapper;
	
	public CQLFHIR2ECRService(FHIRFilterService fhirFilterService) {
		this.fhirFilterService = fhirFilterService;
		parser2 = FhirContext.forDstu2().newJsonParser();
		objectMapper = new ObjectMapper();
	}
	
	public ECR CQLFHIRResultsToECR(ArrayNode cqlResults) {
		ECR ecr = new ECR();
		for(JsonNode result:cqlResults) {
			log.debug("Result:"+result.toString());
			if(result.get("resultType") != null) {
				String resultType = result.get("resultType").asText();
				String filteredResults = "";
				switch(resultType) {
				case "Patient":
					filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
					if(!filteredResults.equalsIgnoreCase("{}")) {
						Patient patient = (Patient)parser2.parseResource(filteredResults);
						handlePatient(ecr,patient);
						break;
					}
				case "FhirBundleCursorStu3":
				case "List":
					handleList(ecr,result.get("result").asText());
					break;
				case "Condition":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Condition condition = (Condition)parser2.parseResource(filteredResults);
						handleCondition(ecr,condition);
						break;
					}
				case "MedicationAdministration":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationAdministration medicationAdministration = (MedicationAdministration)parser2.parseResource(filteredResults);
						handleMedicationAdministration(ecr,medicationAdministration);
						break;
					}
				case "MedicationDispense":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationDispense medicationDispense = (MedicationDispense)parser2.parseResource(filteredResults);
						handleMedicationDispense(ecr,medicationDispense);
						break;
					}
				case "MedicationOrder":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationOrder medicationOrder = (MedicationOrder)parser2.parseResource(filteredResults);
						handleMedicationOrder(ecr,medicationOrder);
						break;
					}
				case "MedicationStatement":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						MedicationStatement medicationStatement = (MedicationStatement)parser2.parseResource(filteredResults);
						handleMedicationStatement(ecr,medicationStatement);
						break;
					}
				case "Observation":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Observation observation = (Observation)parser2.parseResource(filteredResults);
						handleObservation(ecr,observation);
						break;
					}
				case "Procedure":
					if(!filteredResults.equalsIgnoreCase("{}")) {
						filteredResults = fhirFilterService.applyFilter(result.get("result"),false);
						Procedure procedure = (Procedure)parser2.parseResource(filteredResults);
						handleProcedure(ecr,procedure);
						break;
					}
				case "String":
				case "StringType":
					addStringResultByResultKey(ecr,result);
					break;
				}
			}
		}
		return ecr;
	}
	
	void handleList(ECR ecr,String list) {
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
				IResource resource = (IResource)parser2.parseResource(filteredResource);
				inputBundle.addEntry(new Entry().setResource(resource));
			}
			handleBundle(ecr,inputBundle);
		}
	}
	
	void handleBundle(ECR ecr, Bundle bundle) {
		for(Entry entry : bundle.getEntry()) {
			IResource resource = entry.getResource();
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
				if(resource instanceof MedicationDispense) {
					handleMedicationDispense(ecr,(MedicationDispense)resource);
				}
				if(resource instanceof MedicationOrder) {
					handleMedicationOrder(ecr,(MedicationOrder)resource);
				}
				if(resource instanceof MedicationStatement) {
					handleMedicationStatement(ecr,(MedicationStatement)resource);
				}
				if(resource instanceof Condition) {
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

	void handlePatient(ECR ecr, ca.uhn.fhir.model.dstu2.resource.Patient patient) {
		log.info("PATIENT --- ");
		ecr.getPatient().setbirthDate(patient.getBirthDate().toString());
		IDatatype deceasedValue = patient.getDeceased();
		if (deceasedValue != null && deceasedValue instanceof DateDt) {
			ecr.getPatient().setdeathDate(DateUtil.dateToStdString(((DateDt) deceasedValue).getValue()));
		}
		ecr.getPatient().setsex(patient.getGender());
	}

	void handleRelatedPerson(ECR ecr, RelatedPerson relatedPerson) {
		log.info("RELATED PERSON --- ");
		Name nameToSearch = new Name(relatedPerson.getName().getFamily().get(0).getValue(),
				relatedPerson.getName().getGiven().get(0).getValue());
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
		for (ContactPointDt contact : provider.getTelecom()) {
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
		CodeableConcept ecrCode = new CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONADMINISTRATION --- Trying medicationAdministration: "
				+ medicationAdministration.getId());
		IDatatype medicationCodeUntyped = medicationAdministration.getMedication();
		log.info("MEDICATIONADMINISTRATION --- medication code element class: "
				+ medicationCodeUntyped.getClass());
		if (medicationCodeUntyped instanceof CodeableConceptDt) {
			CodeableConceptDt code = (CodeableConceptDt) medicationCodeUntyped;
			log.info("MEDICATIONADMINISTRATION --- Trying code with this many codings: "
					+ code.getCoding().size());
			for (CodingDt coding : code.getCoding()) {
				log.info("MEDICATIONADMINISTRATION --- Trying coding: " + coding.getDisplay());
				CodeableConcept concept = FHIRCoding2ECRConcept(coding);
				log.info("MEDICATIONADMINISTRATION --- Translated to ECRconcept:" + concept.toString());
				ecrMedication.setCode(concept.getcode());
				ecrMedication.setSystem(concept.getsystem());
				ecrMedication.setDisplay(concept.getdisplay());
				ecrCode.setcode(concept.getcode());
				ecrCode.setsystem(concept.getsystem());
				ecrCode.setdisplay(concept.getdisplay());
			}
		}
		if (!medicationAdministration.getDosage().isEmpty()) {
			Dosage ecrDosage = new Dosage();
			ecrDosage.setValue(medicationAdministration.getDosage().getQuantity().getValue().toString());
			ecrDosage.setUnit(medicationAdministration.getDosage().getQuantity().getUnit());
			ecrMedication.setDosage(ecrDosage);
		}
		if (!medicationAdministration.getEffectiveTime().isEmpty()) {
			ecrMedication.setDate(HAPIFHIRUtil.getDate(medicationAdministration.getEffectiveTime()).toString());
		}
		log.info("MEDICATIONADMINISTRATION --- ECRCode: " + ecrCode);
		if (ControllerUtils.isSTIMed(ecrCode)
				&& !ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
			log.info("MEDICATIONADMINISTRATION --- Found New Entry: " + ecrCode);
			ecr.getPatient().getMedicationProvided().add(ecrMedication);
		} else {
			log.info("MEDICATIONADMINISTRATION --- Didn't Match or found duplicate! " + ecrCode);
		}

		if (!medicationAdministration.getReasonGiven().isEmpty()) {
			for (CodeableConceptDt reason : medicationAdministration.getReasonGiven()) {
				handleConditionConceptCode(ecr, reason);
			}
		}
		
	}

	void handleMedicationDispense(ECR ecr, MedicationDispense medicationDispense) {
		CodeableConcept ecrCode = new CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONDISPENSE --- Trying medicationDispense: " + medicationDispense.getId());

		IDatatype medicationCodeUntyped = medicationDispense.getMedication();
		if (medicationCodeUntyped == null && medicationDispense.getAuthorizingPrescription() != null) {
			medicationCodeUntyped = ((MedicationOrder) medicationDispense.getAuthorizingPrescription().get(0)
					.getResource()).getMedication();
		}
		if (medicationCodeUntyped == null) {
			log.info("MEDICATIONDISPENSE --- FAILED TO FIND MEDICATION - SKIPPING!!");
			return;
		}
		log.info("MEDICATIONDISPENSE --- medication code element class: " + medicationCodeUntyped.getClass());

		CodeableConceptDt code = null;

		if (medicationCodeUntyped instanceof CodeableConceptDt) {
			code = (CodeableConceptDt) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof ResourceReferenceDt) {
			code = ((Medication) ((ResourceReferenceDt) medicationCodeUntyped).getResource()).getCode();
		}
		if (code != null) {
			log.info("MEDICATIONDISPENSE --- Trying code with this many codings: " + code.getCoding().size());
			for (CodingDt coding : code.getCoding()) {
				log.info("MEDICATIONDISPENSE --- Trying coding: " + coding.getDisplay());
				CodeableConcept concept = FHIRCoding2ECRConcept(coding);

				log.info("\n----------> MEDICATIONDISPENSE --- Translated to ECRconcept:" + concept.toString());
				ecrMedication.setCode(concept.getcode());
				ecrMedication.setSystem(concept.getsystem());
				ecrMedication.setDisplay(concept.getdisplay());
				ecrCode.setcode(concept.getcode());
				ecrCode.setsystem(concept.getsystem());
				ecrCode.setdisplay(concept.getdisplay());

				if (ControllerUtils.isSTIMed(ecrCode)) {
					break; // Found a code already so stop here.
				}
			}
			if (ControllerUtils.isSTIMed(ecrCode)) {
				for (ca.uhn.fhir.model.dstu2.resource.MedicationDispense.DosageInstruction dosageInstruction : medicationDispense
						.getDosageInstruction()) {
					Dosage ecrDosage = new Dosage();
					IDatatype doseUntyped = dosageInstruction.getDose();
					if (doseUntyped != null) {
						log.info("MEDICATIONDISPENSE --- Found Dosage: " + doseUntyped.toString());
						if (doseUntyped instanceof SimpleQuantityDt) {
							SimpleQuantityDt doseTyped = (SimpleQuantityDt) doseUntyped;
							log.info("MEDICATIONDISPENSE --- Dosage is of SimpleQuentityDt Type");
							ecrDosage.setValue(doseTyped.getValue().toString());
							ecrDosage.setUnit(doseTyped.getUnit());
							ecrMedication.setDosage(ecrDosage);
						}
						String periodUnit = dosageInstruction.getTiming().getRepeat().getPeriodUnits();
						BigDecimal period = dosageInstruction.getTiming().getRepeat().getPeriod();
						Integer frequency = dosageInstruction.getTiming().getRepeat().getFrequency();
						String commonFrequency = "" + frequency + " times per " + period + " " + periodUnit;
						log.info("MEDICATIONDISPENSE --- Found Frequency: " + commonFrequency);
						ecrMedication.setFrequency(commonFrequency);
					} else {
						log.info("MEDICATIONDISPENSE --- Not Found");
					}
				}
				Date timeDispensed = medicationDispense.getWhenHandedOver();
				log.info("MEDICATIONDISPENSE --- Found Handed Over Date: " + timeDispensed);
				if (timeDispensed != null) {
					ecrMedication.setDate(DateUtil.dateTimeToStdString(timeDispensed));
				}
				log.info("MEDICATIONDISPENSE --- ECRCode: " + ecrCode);
				if (!ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
					log.info("=======>MEDICATIONDISPENSE --- Found New Entry and added to ECR: " + ecrCode);
					ecr.getPatient().getMedicationProvided().add(ecrMedication);
				} else {
					log.info("MEDICATIONDISPENSE --- Didn't Match or found duplicate! " + ecrCode);
				}
			} else {
				log.info("MEDICATIONDISPENSE --- Didn't Match or found duplicate! " + ecrCode);
			}
		} else {
			log.info("FAILED TO FIND MEDICATION CODE.");
		}
	}

	void handleMedicationOrder(ECR ecr, MedicationOrder medicationOrder) {
		CodeableConcept ecrCode = new CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONORDER --- Trying medicationOrder: " + medicationOrder.getId());
		IDatatype medicationCodeUntyped = medicationOrder.getMedication();
		log.info("MEDICATIONORDER --- medication code element class: " + medicationCodeUntyped.getClass());

		CodeableConceptDt code = null;

		if (medicationCodeUntyped instanceof CodeableConceptDt) {
			code = (CodeableConceptDt) medicationCodeUntyped;
		} else if (medicationCodeUntyped instanceof ResourceReferenceDt) {
			code = ((Medication) ((ResourceReferenceDt) medicationCodeUntyped).getResource()).getCode();
		}
		if (code != null) {
			log.info("MEDICATIONORDER --- Trying code with this many codings: " + code.getCoding().size());
			for (CodingDt coding : code.getCoding()) {
				log.info("MEDICATIONORDER --- Trying coding: " + coding.getDisplay());
				CodeableConcept concept = FHIRCoding2ECRConcept(coding);
				log.info("MEDICATIONORDER --- Translated to ECRconcept:" + concept.toString());
				ecrMedication.setCode(concept.getcode());
				ecrMedication.setSystem(concept.getsystem());
				ecrMedication.setDisplay(concept.getdisplay());
				ecrCode.setcode(concept.getcode());
				ecrCode.setsystem(concept.getsystem());
				ecrCode.setdisplay(concept.getdisplay());
				if (ControllerUtils.isSTIMed(ecrCode)) {
					break; // Found a code already so stop here.
				}
			}
		}
		if (ControllerUtils.isSTIMed(ecrCode)) {
			for (DosageInstruction dosageInstruction : medicationOrder.getDosageInstruction()) {
				Dosage ecrDosage = new Dosage();
				IDatatype doseUntyped = dosageInstruction.getDose();
				if (doseUntyped != null) {
					log.info("MEDICATIONORDER --- Found Dosage: " + doseUntyped.toString());
					if (doseUntyped instanceof SimpleQuantityDt) {
						SimpleQuantityDt doseTyped = (SimpleQuantityDt) doseUntyped;
						log.info("MEDICATIONORDER --- Dosage is of SimpleQuentityDt Type");
						ecrDosage.setValue(doseTyped.getValue().toString());
						ecrDosage.setUnit(doseTyped.getUnit());
						ecrMedication.setDosage(ecrDosage);
					}
					String periodUnit = dosageInstruction.getTiming().getRepeat().getPeriodUnits();
					BigDecimal period = dosageInstruction.getTiming().getRepeat().getPeriod();
					Integer frequency = dosageInstruction.getTiming().getRepeat().getFrequency();
					// String commonFrequency= "" + frequency + " times per " + period + " " +
					// periodUnit;
					// log.info("MEDICATIONORDER --- Found Frequency: " + commonFrequency);
					// ecrMedication.setFrequency(commonFrequency);
				} else {
					log.info("MEDICATIONORDER --- DOSE NOT FOUND.");
				}
			}

			log.info("MEDICATIONORDER --- ECRCode: " + ecrCode);
			if (ControllerUtils.isSTIMed(ecrCode)
					&& !ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
				log.info("MEDICATIONORDER --- Found New Entry: " + ecrCode);
				ecr.getPatient().getMedicationProvided().add(ecrMedication);
			} else {
				log.info("MEDICATIONORDER --- Didn't Match or found duplicate! " + ecrCode);
			}
			// String periodUnit =
			// dosageInstruction.getTiming().getRepeat().getPeriodUnits();
			// BigDecimal period = dosageInstruction.getTiming().getRepeat().getPeriod();
			// Integer frequency = dosageInstruction.getTiming().getRepeat().getFrequency();
			// /*String commonFrequency= "" + frequency + " times per " + period + " " +
			// periodUnit;
			// log.info("MEDICATIONORDER --- Found Frequency: " + commonFrequency);
			// ecrMedication.setFrequency(commonFrequency); */
		}

		PeriodDt period = medicationOrder.getDispenseRequest().getValidityPeriod();
		if ( period != null && period.getStart() != null ) {
			log.info("MEDICATIONORDER --- Found Validity Period: " + period.getStart().toLocaleString());
			ecrMedication.setDate(period.getStart().toString());
		}
		log.info("MEDICATIONORDER --- ECRCode: " + ecrCode);
		if (ControllerUtils.isSTIMed(ecrCode)
				&& !ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
			log.info("MEDICATIONORDER --- Found New Entry: " + ecrCode);
			ecr.getPatient().getMedicationProvided().add(ecrMedication);
		} else {
			log.info("MEDICATIONORDER --- Didn't Match or found duplicate! " + ecrCode);
		}
		if (medicationOrder.getReason() != null && !medicationOrder.getReason().isEmpty()) {
			if (medicationOrder.getReason() instanceof CodeableConceptDt) {
				handleConditionConceptCode(ecr, (CodeableConceptDt) medicationOrder.getReason());
			}
			if (medicationOrder.getReason() != null && !medicationOrder.getReason().isEmpty()) {
				if (medicationOrder.getReason() instanceof CodeableConceptDt) {
					handleConditionConceptCode(ecr, (CodeableConceptDt) medicationOrder.getReason());
				}
			}
		} else {
			log.info("MEDICATIONORDER --- Didn't Match  " + ecrCode);
		}
	}

	void handleMedicationStatement(ECR ecr, MedicationStatement medicationStatement) {
		CodeableConcept ecrCode = new CodeableConcept();
		gatech.edu.STIECR.JSON.Medication ecrMedication = new gatech.edu.STIECR.JSON.Medication();
		log.info("MEDICATIONSTATEMENT  --- Trying medicationOrder: " + medicationStatement.getId());
		IDatatype medicationCodeUntyped = medicationStatement.getMedication();
		log.info("MEDICATIONSTATEMENT  --- medication code element class: " + medicationCodeUntyped.getClass());
		if (medicationCodeUntyped instanceof CodeableConceptDt) {
			CodeableConceptDt code = (CodeableConceptDt) medicationCodeUntyped;
			log.info("MEDICATIONSTATEMENT  --- Trying code with this many codings: " + code.getCoding().size());
			for (CodingDt coding : code.getCoding()) {
				log.info("MEDICATIONSTATEMENT  --- Trying coding: " + coding.getDisplay());
				CodeableConcept concept = FHIRCoding2ECRConcept(coding);
				log.info("MEDICATIONSTATEMENT  --- Translated to ECRconcept:" + concept.toString());
				ecrMedication.setCode(concept.getcode());
				ecrMedication.setSystem(concept.getsystem());
				ecrMedication.setDisplay(concept.getdisplay());
				ecrCode.setcode(concept.getcode());
				ecrCode.setsystem(concept.getsystem());
				ecrCode.setdisplay(concept.getdisplay());
			}
		}
		if (!medicationStatement.getDosage().isEmpty()) {
			Dosage ecrDosage = new Dosage();
			IDatatype dosageQuantityUntyped = medicationStatement.getDosage().get(0).getQuantity();
			if (dosageQuantityUntyped instanceof SimpleQuantityDt) {
				SimpleQuantityDt dosageQuantity = (SimpleQuantityDt) dosageQuantityUntyped;
				ecrDosage.setValue(dosageQuantity.getValue().toString());
				ecrDosage.setUnit(dosageQuantity.getUnit().toString());
			} else
			if (dosageQuantityUntyped instanceof RangeDt) {
				RangeDt dosageRange = (RangeDt) dosageQuantityUntyped;
				BigDecimal high = dosageRange.getHigh().getValue();
				BigDecimal low = dosageRange.getLow().getValue();
				BigDecimal mean = high.add(low);
				mean = mean.divide(new BigDecimal(2));
				ecrDosage.setValue(mean.toString());
				ecrDosage.setUnit(dosageRange.getHigh().getUnit());
				ecrMedication.setDosage(ecrDosage);
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
		if (ControllerUtils.isSTIMed(ecrCode)
				&& !ecr.getPatient().getMedicationProvided().contains(ecrMedication)) {
			log.info("MEDICATIONSTATEMENT  --- Found New Entry: " + ecrCode);
			ecr.getPatient().getMedicationProvided().add(ecrMedication);
		} else {
			log.info("MEDICATIONSTATEMENT  --- Didn't Match or found duplicate! " + ecrCode);
		}
		if (!medicationStatement.getReasonForUse().isEmpty()) {
			if (medicationStatement.getReasonForUse() instanceof CodeableConceptDt) {
				handleConditionConceptCode(ecr,
						(CodeableConceptDt) medicationStatement.getReasonForUse());
			}
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
			log.info("Adding Immunization For " + immunization.getId().getValueAsString());
			ecr.getPatient().getimmunizationHistory().add(ecrImmunization);
		}
	}

	public void handleConditionConceptCode(ECR ecr, CodeableConceptDt code) {
		log.info("CONDITION --- Trying code with this many codings: " + code.getCoding().size());
		for (CodingDt coding : code.getCoding()) {
			log.info("CONDITION --- Trying coding: " + coding.getDisplay());
			CodeableConcept concept = FHIRCoding2ECRConcept(coding);
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
		if (condition.getAbatement() != null) {
			Date abatementDate = HAPIFHIRUtil.getDate(condition.getAbatement());
			if (abatementDate != null & abatementDate.compareTo(new Date()) <= 0) {
				log.info("CONDITION --- Found abatement date of: " + abatementDate);
				log.info("CONDITION --- Condition is not current, ignoring condition.");
				return;
			}
		}
	
		Date onsetDate = HAPIFHIRUtil.getDate(condition.getOnset());
		if (onsetDate == null) {
			onsetDate = condition.getDateRecorded();
		}
		Date ecrDate = null;
		try {
			String onsetDateStr = ecr.getPatient().getdateOfOnset();
			ecrDate = DateUtil.parse(onsetDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		CodeableConceptDt code = condition.getCode();
		log.info("CONDITION --- Trying code with this many codings: " + code.getCoding().size());
		for (CodingDt coding : code.getCoding()) {
			log.info("CONDITION --- Trying coding: " + coding.getDisplay());
			CodeableConcept concept = FHIRCoding2ECRConcept(coding);
			log.info("CONDITION --- Translated to ECRconcept:" + concept.toString());
			if (ControllerUtils.isSTIDiagnosisCode(concept) &&
					diagnosisContainsCodeableConcept(ecr.getPatient().getDiagnosis(),concept)) {
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
		for (CodeableConceptDt reason : encounter.getReason()) {
			for (CodingDt coding : reason.getCoding()) {
				CodeableConcept concept = FHIRCoding2ECRConcept(coding);
				if (!ecr.getPatient().getsymptoms().contains(concept)) {
					ecr.getPatient()
							.setvisitDateTime(DateUtil.dateTimeToStdString(encounter.getPeriod().getStart()));
				}
				// TODO: Figure out the right strategy for mapping an Onset
				// TODO: distinguish between symptom list and diagnosis list here
				// TODO: Map Pregnant from encounters
			}
		}
	}

	void handleObservation(ECR ecr, Observation observation) {
		log.info("OBSERVATION --- Trying observation: " + observation.getId());
		CodeableConceptDt code = observation.getCode();
		for (CodingDt coding : code.getCoding()) {
			if (coding.getCode().equalsIgnoreCase("laboratory")) { // HIT! Found a lab result
				LabOrderCode labOrder = new LabOrderCode();
				labOrder.setcode(coding.getCode());
				labOrder.setdisplay(coding.getDisplay());
				labOrder.setsystem(coding.getSystem());
				LabResult labResult = new LabResult();
				IDatatype untypedValue = observation.getValue();
				if (untypedValue instanceof QuantityDt) {
					labResult.setValue(((QuantityDt) untypedValue).getValue().toString());
				} else
				if (untypedValue instanceof CodeableConcept) {
					labResult.setValue(((CodeableConcept) untypedValue).getdisplay());
				} else
				if (untypedValue instanceof StringDt) {
					labResult.setValue(((StringDt) untypedValue).toString());
				} else
				if (untypedValue instanceof RangeDt) {
					RangeDt range = (RangeDt) untypedValue;
					labResult.setValue("High:" + range.getHigh() + ";low:" + range.getLow());
				} else
				if (untypedValue instanceof RatioDt) {
					RatioDt ratio = (RatioDt) untypedValue;
					labResult.setValue(
							ratio.getNumerator().toString() + "/" + ratio.getDenominator().toString());
				} else
				if (untypedValue instanceof SampledDataDt) {
					labResult.setValue(((SampledDataDt) untypedValue).getData());
				} else
				if (untypedValue instanceof TimeDt) {
					labResult.setValue(((TimeDt) untypedValue).getValue());
				} else
				if (untypedValue instanceof DateTimeDt) {
					labResult.setValue(((DateTimeDt) untypedValue).getValueAsString());
				} else
				if (untypedValue instanceof PeriodDt) {
					PeriodDt period = (PeriodDt) untypedValue;
					labResult.setValue(
							"Start:" + period.getStart().toString() + ";End" + period.getEnd().toString());
				}
				labOrder.getLaboratory_Results().add(labResult);
			}
		}
	}

	void handleProcedure(ECR ecr,Procedure procedure) {
		log.info("PROCEDURE --- Trying procedure: " + procedure.getId());
		if (procedure.getReason() != null && !procedure.getReason().isEmpty()) {
			if (procedure.getReason() instanceof CodeableConceptDt) {
				handleConditionConceptCode(ecr, (CodeableConceptDt) procedure.getReason());
			}
		}
	}

	void updateParentGuardian(ParentGuardian pg, RelatedPerson rp) {
		for (ContactPointDt contact : rp.getTelecom()) {
			if (contact.getSystem().equals("Phone") && pg.getphone().isEmpty()) {
				pg.setphone(contact.getValue());
			} else
			if (contact.getSystem().equals("Email") && pg.getemail().isEmpty()) {
				pg.setemail(contact.getValue());
			}
		}
	}

	public static CodeableConcept FHIRCoding2ECRConcept(CodingDt fhirCoding) {
		CodeableConcept ecrConcept = new CodeableConcept();
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
		case "20A.Patient.Name.family":
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
			ecr.getPatient().setrace(new CodeableConcept("","",value));
			break;
		case "29.Patient.Ethnicity":
			ecr.getPatient().setethnicity(new CodeableConcept("","",value));
			break;
		case "30.Patient.Preferred_Language":
			ecr.getPatient().setpreferredLanguage(new CodeableConcept("","",value));
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
	
	public static boolean diagnosisContainsCodeableConcept(List<Diagnosis> listDiagnosis,CodeableConcept ecrConcept) {
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
}
