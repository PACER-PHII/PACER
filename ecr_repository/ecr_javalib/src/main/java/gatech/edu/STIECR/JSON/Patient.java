package gatech.edu.STIECR.JSON;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Patient {
	@JsonProperty("ID")
	private List<TypeableID> id = new ArrayList<TypeableID>();
	@JsonProperty("Name")
	private Name name = new Name();
	@JsonProperty("Parents_Guardians")
	private List<ParentGuardian> parentsGuardians = new ArrayList<ParentGuardian>();
	@JsonProperty("Street_Address")
	private String streetAddress = "";
	@JsonProperty("Birth_Date")
	private String birthDate = "";
	@JsonProperty("Sex")
	private String sex = "";
	@JsonProperty("PatientClass")
	private String patientClass = "";
	@JsonProperty("Race")
	private CodeableConcept race = new CodeableConcept();
	@JsonProperty("Ethnicity")
	private CodeableConcept ethnicity = new CodeableConcept();
	@JsonProperty("Preferred_Language")
	private CodeableConcept preferredLanguage = new CodeableConcept();
	@JsonProperty("Occupation")
	private String occupation = "";
	@JsonProperty("Pregnant")
	private boolean pregnant = false;
	@JsonProperty("Travel_History")
	private List<String> travelHistory = new ArrayList<String>();
	//@JsonUnwrapped
	@JsonProperty("Insurance_Type")
	private CodeableConcept insuranceType = new CodeableConcept();
	@JsonProperty("Immunization_History")
	private List<ImmunizationHistory> immunizationHistory = new ArrayList<ImmunizationHistory>();
	@JsonProperty("Visit_DateTime")
	private String visitDateTime = "";
	@JsonProperty("Admission_DateTime")
	private String admissionDateTime = "";
	@JsonProperty("Date_Of_Onset")
	private String dateOfOnset = "";
	@JsonProperty("Symptoms")
	private List<CodeableConcept> symptoms = new ArrayList<CodeableConcept>();
	@JsonProperty("Lab_Order_Code")
	private List<LabOrderCode> labOrderCode = new ArrayList<LabOrderCode>();
	@JsonProperty("Placer_Order_Code")
	private String placerOrderCode = "";
	@JsonProperty("Diagnosis")
	private Diagnosis diagnosis = new Diagnosis();
	@JsonProperty("Medication Provided")
	private List<Medication> medicationProvided = new ArrayList<Medication>();
	@JsonProperty("Death_Date")
	private String deathDate = "";
	@JsonProperty("Date_Discharged")
	private String dateDischarged = "";
	@JsonProperty("Laboratory_Results")
	private List<LabResult> laboratoryResults = new ArrayList<LabResult>();
	@JsonProperty("Trigger_Code")
	private List<CodeableConcept> triggerCode = new ArrayList<CodeableConcept>();
	@JsonProperty("Lab_Tests_Performed")
	private List<TestResult> labTestsPerformed = new ArrayList<TestResult>();

	public Patient() { }
	
	public List<TypeableID> getid() {
		return id;
	}

	public void setid(List<TypeableID> iD) {
		this.id = iD;
	}

	public Name getname() {
		return name;
	}

	public void setname(Name name) {
		this.name = name;
	}
	
	public void setname(String given, String family) {
		
		this.name = new Name(given,family);
	}
	
	public List<ParentGuardian> getparentsGuardians() {
		return parentsGuardians;
	}

	public void setparentsGuardians(List<ParentGuardian> parentsGuardians) {
		this.parentsGuardians = parentsGuardians;
	}

	public String getstreetAddress() {
		return streetAddress;
	}

	public void setstreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	public String getbirthDate() {
		return birthDate;
	}

	public void setbirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getsex() {
		return sex;
	}

	public void setsex(String sex) {
		this.sex = sex;
	}

	public String getpatientClass() {
		return patientClass;
	}

	public void setpatientClass(String patientClass) {
		this.patientClass = patientClass;
	}

	public CodeableConcept getrace() {
		return race;
	}

	public void setrace(CodeableConcept race) {
		this.race = race;
	}

	public CodeableConcept getethnicity() {
		return ethnicity;
	}

	public void setethnicity(CodeableConcept ethnicity) {
		this.ethnicity = ethnicity;
	}

	public CodeableConcept getpreferredLanguage() {
		return preferredLanguage;
	}

	public void setpreferredLanguage(CodeableConcept preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public String getoccupation() {
		return occupation;
	}

	public void setoccupation(String occupation) {
		this.occupation = occupation;
	}

	public boolean ispregnant() {
		return pregnant;
	}

	public void setpregnant(boolean pregnant) {
		this.pregnant = pregnant;
	}

	public List<String> gettravelHistory() {
		return travelHistory;
	}

	public void settravelHistory(List<String> travelHistory) {
		this.travelHistory = travelHistory;
	}

	public CodeableConcept getinsuranceType() {
		return insuranceType;
	}

	public void setinsuranceType(CodeableConcept insuranceType) {
		this.insuranceType = insuranceType;
	}

	public List<ImmunizationHistory> getimmunizationHistory() {
		return immunizationHistory;
	}

	public void setimmunizationHistory(List<ImmunizationHistory> immunizationHistory) {
		this.immunizationHistory = immunizationHistory;
	}

	public String getvisitDateTime() {
		return visitDateTime;
	}

	public void setvisitDateTime(String visitDateTime) {
		this.visitDateTime = visitDateTime;
	}

	public String getadmissionDateTime() {
		return admissionDateTime;
	}

	public void setadmissionDateTime(String admissionDateTime) {
		this.admissionDateTime = admissionDateTime;
	}

	public String getdateOfOnset() {
		return dateOfOnset;
	}

	public void setdateOfOnset(String dateOfOnset) {
		this.dateOfOnset = dateOfOnset;
	}

	public List<CodeableConcept> getsymptoms() {
		return symptoms;
	}

	public void setsymptoms(List<CodeableConcept> symptoms) {
		this.symptoms = symptoms;
	}

	public List<LabOrderCode> getlabOrderCode() {
		return labOrderCode;
	}

	public void setlabOrderCode(List<LabOrderCode> labOrderCode) {
		this.labOrderCode = labOrderCode;
	}

	public String getplacerOrderCode() {
		return placerOrderCode;
	}

	public void setplacerOrderCode(String placerOrderCode) {
		this.placerOrderCode = placerOrderCode;
	}

	public Diagnosis getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(Diagnosis diagnosis) {
		this.diagnosis = diagnosis;
	}
	
	public List<Medication> getMedicationProvided() {
		return medicationProvided;
	}
	
	public void setMedicationProvided(List<Medication> medicationProvided) {
		this.medicationProvided = medicationProvided;
	}

	public String getdeathDate() {
		return deathDate;
	}

	public void setdeathDate(String deathDate) {
		this.deathDate = deathDate;
	}

	public String getdateDischarged() {
		return dateDischarged;
	}

	public void setdateDischarged(String dateDischarged) {
		this.dateDischarged = dateDischarged;
	}

	public List<LabResult> getlaboratoryResults() {
		return laboratoryResults;
	}

	public void setlaboratoryResults(List<LabResult> laboratoryResults) {
		this.laboratoryResults = laboratoryResults;
	}

	public List<CodeableConcept> gettriggerCode() {
		return triggerCode;
	}

	public void settriggerCode(List<CodeableConcept> triggerCode) {
		this.triggerCode = triggerCode;
	}

	public List<TestResult> getlabTestsPerformed() {
		return labTestsPerformed;
	}

	public void setlabTestsPerformed(List<TestResult> labTestsPerformed) {
		this.labTestsPerformed = labTestsPerformed;
	}
	
	public void update(Patient newPatient) {
		if(newPatient.getname() != null && (this.name == null || !newPatient.getname().equals(this.name))) {
			this.name = newPatient.getname();
		}
		
		for(TypeableID newID: newPatient.getid()) {
			if(!this.id.contains(newID)) {
				this.id.add(newID);
			}
		}
		
		for(ParentGuardian newPG: newPatient.getparentsGuardians()) {
			if(!this.parentsGuardians.contains(newPG)) {
				this.parentsGuardians.add(newPG);
			}
		}
		
		if(!newPatient.getstreetAddress().isEmpty()) {
			this.streetAddress = newPatient.getstreetAddress();
		}
		if(!newPatient.getbirthDate().isEmpty()) {
			this.birthDate = newPatient.getbirthDate();
		}
		if(!newPatient.getsex().isEmpty()) {
			this.sex = newPatient.getsex();
		}
		if(!newPatient.getpatientClass().isEmpty()) {
			this.patientClass = newPatient.getpatientClass();
		}
		if(newPatient.getrace() != null && (this.race == null || !newPatient.getrace().equals(this.race))) {
			this.race = newPatient.getrace();
		}
		if(newPatient.getethnicity() != null && (this.ethnicity == null || !newPatient.getethnicity().equals(this.ethnicity))) {
			this.ethnicity = newPatient.getethnicity();
		}
		if(newPatient.getpreferredLanguage() != null && (this.preferredLanguage == null || !newPatient.getpreferredLanguage().equals(this.preferredLanguage))) {
			this.preferredLanguage = newPatient.getpreferredLanguage();
		}
		if(!newPatient.getoccupation().isEmpty()) {
			this.occupation = newPatient.getoccupation();
		}
		if(newPatient.ispregnant() != this.pregnant) {
			this.pregnant = newPatient.ispregnant();
		}
		for(String travelHistory : newPatient.gettravelHistory()) {
			if(!this.travelHistory.contains(travelHistory)) {
				this.travelHistory.add(travelHistory);
			}
		}
		if(newPatient.getinsuranceType() != null && (this.insuranceType == null || !newPatient.getinsuranceType().equals(this.insuranceType))) {
			this.insuranceType = newPatient.getinsuranceType();
		}
		for(ImmunizationHistory immunizationEvent : newPatient.getimmunizationHistory()) {
			if(!this.immunizationHistory.contains(immunizationEvent)) {
				this.immunizationHistory.add(immunizationEvent);
			}
		}
		if(!newPatient.getvisitDateTime().isEmpty()) {
			this.visitDateTime = newPatient.getvisitDateTime();
		}
		if(!newPatient.getadmissionDateTime().isEmpty()) {
			this.admissionDateTime = newPatient.getadmissionDateTime();
		}
		if(!newPatient.getdateOfOnset().isEmpty()) {
			this.dateOfOnset = newPatient.getdateOfOnset();
		}
		for(CodeableConcept symptom : newPatient.getsymptoms()) {
			if(!this.symptoms.contains(symptom)) {
				this.symptoms.add(symptom);
			}
		}
		for(LabOrderCode newLabOrderCode : newPatient.getlabOrderCode()) {
			if(!this.labOrderCode.contains(newLabOrderCode)) {
				this.labOrderCode.add(newLabOrderCode);
			}
		}
		if(!newPatient.getplacerOrderCode().isEmpty()) {
			this.placerOrderCode = newPatient.getplacerOrderCode();
		}
		if(newPatient.getDiagnosis() != null && (this.diagnosis == null || !newPatient.diagnosis.equals(this.diagnosis))) {
			this.diagnosis = newPatient.getDiagnosis();
		}
		for(Medication medication : newPatient.getMedicationProvided()) {
			if(!this.medicationProvided.contains(medication)) {
				this.medicationProvided.add(medication);
			}
		}
		if(!newPatient.getdeathDate().isEmpty()) {
			this.deathDate = newPatient.getdeathDate();
		}
		if(!newPatient.getdateDischarged().isEmpty()) {
			this.dateDischarged = newPatient.getdateDischarged();
		}
		for(LabResult newLabResult : newPatient.getlaboratoryResults()) {
			if(!this.laboratoryResults.contains(newLabResult)) {
				this.symptoms.add(newLabResult);
			}
		}
		for(CodeableConcept newTriggerCode : newPatient.gettriggerCode()) {
			if(!this.triggerCode.contains(newTriggerCode)) {
				this.triggerCode.add(newTriggerCode);
			}
		}
		for(CodeableConcept newTriggerCode : newPatient.gettriggerCode()) {
			if(!this.triggerCode.contains(newTriggerCode)) {
				this.triggerCode.add(newTriggerCode);
			}
		}
		for(TestResult newTestResult : newPatient.getlabTestsPerformed()) {
			if(!this.labTestsPerformed.contains(newTestResult)) {
				this.labTestsPerformed.add(newTestResult);
			}
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((admissionDateTime == null) ? 0 : admissionDateTime.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((dateDischarged == null) ? 0 : dateDischarged.hashCode());
		result = prime * result + ((dateOfOnset == null) ? 0 : dateOfOnset.hashCode());
		result = prime * result + ((deathDate == null) ? 0 : deathDate.hashCode());
		result = prime * result + ((diagnosis == null) ? 0 : diagnosis.hashCode());
		result = prime * result + ((ethnicity == null) ? 0 : ethnicity.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((immunizationHistory == null) ? 0 : immunizationHistory.hashCode());
		result = prime * result + ((insuranceType == null) ? 0 : insuranceType.hashCode());
		result = prime * result + ((labOrderCode == null) ? 0 : labOrderCode.hashCode());
		result = prime * result + ((labTestsPerformed == null) ? 0 : labTestsPerformed.hashCode());
		result = prime * result + ((laboratoryResults == null) ? 0 : laboratoryResults.hashCode());
		result = prime * result + ((medicationProvided == null) ? 0 : medicationProvided.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((occupation == null) ? 0 : occupation.hashCode());
		result = prime * result + ((parentsGuardians == null) ? 0 : parentsGuardians.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((patientClass == null) ? 0 : patientClass.hashCode());
		result = prime * result + ((placerOrderCode == null) ? 0 : placerOrderCode.hashCode());
		result = prime * result + ((preferredLanguage == null) ? 0 : preferredLanguage.hashCode());
		result = prime * result + (pregnant ? 1231 : 1237);
		result = prime * result + ((race == null) ? 0 : race.hashCode());
		result = prime * result + ((sex == null) ? 0 : sex.hashCode());
		result = prime * result + ((streetAddress == null) ? 0 : streetAddress.hashCode());
		result = prime * result + ((symptoms == null) ? 0 : symptoms.hashCode());
		result = prime * result + ((travelHistory == null) ? 0 : travelHistory.hashCode());
		result = prime * result + ((triggerCode == null) ? 0 : triggerCode.hashCode());
		result = prime * result + ((visitDateTime == null) ? 0 : visitDateTime.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Patient))
			return false;
		Patient other = (Patient) obj;
		if (admissionDateTime == null) {
			if (other.admissionDateTime != null)
				return false;
		} else if (!admissionDateTime.equals(other.admissionDateTime))
			return false;
		if (birthDate == null) {
			if (other.birthDate != null)
				return false;
		} else if (!birthDate.equals(other.birthDate))
			return false;
		if (dateDischarged == null) {
			if (other.dateDischarged != null)
				return false;
		} else if (!dateDischarged.equals(other.dateDischarged))
			return false;
		if (dateOfOnset == null) {
			if (other.dateOfOnset != null)
				return false;
		} else if (!dateOfOnset.equals(other.dateOfOnset))
			return false;
		if (deathDate == null) {
			if (other.deathDate != null)
				return false;
		} else if (!deathDate.equals(other.deathDate))
			return false;
		if (diagnosis == null) {
			if (other.diagnosis != null)
				return false;
		} else if (!diagnosis.equals(other.diagnosis))
			return false;
		if (ethnicity == null) {
			if (other.ethnicity != null)
				return false;
		} else if (!ethnicity.equals(other.ethnicity))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (immunizationHistory == null) {
			if (other.immunizationHistory != null)
				return false;
		} else if (!immunizationHistory.equals(other.immunizationHistory))
			return false;
		if (insuranceType == null) {
			if (other.insuranceType != null)
				return false;
		} else if (!insuranceType.equals(other.insuranceType))
			return false;
		if (labOrderCode == null) {
			if (other.labOrderCode != null)
				return false;
		} else if (!labOrderCode.equals(other.labOrderCode))
			return false;
		if (labTestsPerformed == null) {
			if (other.labTestsPerformed != null)
				return false;
		} else if (!labTestsPerformed.equals(other.labTestsPerformed))
			return false;
		if (laboratoryResults == null) {
			if (other.laboratoryResults != null)
				return false;
		} else if (!laboratoryResults.equals(other.laboratoryResults))
			return false;
		if (medicationProvided == null) {
			if (other.medicationProvided != null)
				return false;
		} else if (!medicationProvided.equals(other.medicationProvided))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (occupation == null) {
			if (other.occupation != null)
				return false;
		} else if (!occupation.equals(other.occupation))
			return false;
		if (parentsGuardians == null) {
			if (other.parentsGuardians != null)
				return false;
		} else if (!parentsGuardians.equals(other.parentsGuardians))
			return false;
		if (patientClass == null) {
			if (other.patientClass != null)
				return false;
		} else if (!patientClass.equals(other.patientClass))
			return false;
		if (placerOrderCode == null) {
			if (other.placerOrderCode != null)
				return false;
		} else if (!placerOrderCode.equals(other.placerOrderCode))
			return false;
		if (preferredLanguage == null) {
			if (other.preferredLanguage != null)
				return false;
		} else if (!preferredLanguage.equals(other.preferredLanguage))
			return false;
		if (pregnant != other.pregnant)
			return false;
		if (race == null) {
			if (other.race != null)
				return false;
		} else if (!race.equals(other.race))
			return false;
		if (sex == null) {
			if (other.sex != null)
				return false;
		} else if (!sex.equals(other.sex))
			return false;
		if (streetAddress == null) {
			if (other.streetAddress != null)
				return false;
		} else if (!streetAddress.equals(other.streetAddress))
			return false;
		if (symptoms == null) {
			if (other.symptoms != null)
				return false;
		} else if (!symptoms.equals(other.symptoms))
			return false;
		if (travelHistory == null) {
			if (other.travelHistory != null)
				return false;
		} else if (!travelHistory.equals(other.travelHistory))
			return false;
		if (triggerCode == null) {
			if (other.triggerCode != null)
				return false;
		} else if (!triggerCode.equals(other.triggerCode))
			return false;
		if (visitDateTime == null) {
			if (other.visitDateTime != null)
				return false;
		} else if (!visitDateTime.equals(other.visitDateTime))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Patient [id=" + id + ", name=" + name + ", parentsGuardians=" + parentsGuardians + ", streetAddress="
				+ streetAddress + ", birthDate=" + birthDate + ", sex=" + sex + ", patientClass=" + patientClass
				+ ", race=" + race + ", ethnicity=" + ethnicity + ", preferredLanguage=" + preferredLanguage
				+ ", occupation=" + occupation + ", pregnant=" + pregnant + ", travelHistory=" + travelHistory
				+ ", insuranceType=" + insuranceType + ", immunizationHistory=" + immunizationHistory
				+ ", visitDateTime=" + visitDateTime + ", admissionDateTime=" + admissionDateTime + ", dateOfOnset="
				+ dateOfOnset + ", symptoms=" + symptoms + ", labOrderCode=" + labOrderCode + ", placerOrderCode="
				+ placerOrderCode + ", diagnosis=" + diagnosis + ", medicationProvided=" + medicationProvided
				+ ", deathDate=" + deathDate + ", dateDischarged=" + dateDischarged + ", laboratoryResults="
				+ laboratoryResults + ", triggerCode=" + triggerCode + ", labTestsPerformed=" + labTestsPerformed + "]";
	}
	
	public TypeableID getIdMatchingType(String type) {
		for(TypeableID myID: id) {
			if (myID.gettype().equalsIgnoreCase(type))
				return myID;
		}
		return null;
	}
}
