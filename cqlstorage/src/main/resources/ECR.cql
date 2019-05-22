library ChlamydiaECR version '1.0'
using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers
codesystem "SNOMED": 'http://snomed.info/sct'
codesystem "ICD9PROC": 'http://hl7.org/fhir/sid/icd-9-proc'
codesystem "LOINC": 'http://loinc.org'
codesystem "CPT": 'http://www.ama-assn.org/go/cpt'
codesystem "RxNorm": 'http://www.nlm.nih.gov/research/umls/rxnorm'

valueset "Chlamydia_Codes": '2.16.840.1.113883.3.464.1003.112.12.1003'
valueset "Immunization_Codes": '2.16.840.1.113762.1.4.1010.8'
valueset "Macrolides": '2.16.840.1.113883.3.666.5.772'
valueset "Levofloxacin": '2.16.840.1.113762.1.4.1032.69'
valueset "Doxycycline": '2.16.840.1.113762.1.4.1032.66'

define "Pregnancy Diagnoses": Concept { Code '35633007' from "SNOMED", Code '42343007' from "SNOMED" }

define "Chlamydia Symptoms": Concept { Code '49650001' from "SNOMED", Code '2910007' from "SNOMED" }

define "Anemia": Concept { Code '267530009' from "SNOMED" }
define "Furosemide": Concept { Code '282452' from "RxNorm" }

context Patient

define "myFeature": 'myText'
define "Pt": [Patient] Pt
define "Chlamydia Diagnosis": [Condition:"Chlamydia_Codes"]
define "19.Patient.ID": "Pt".identifier[0].value
define "20A.Patient.Name.given": "Pt".name[0].given[0]
define "20B.Patient.Name.family": "Pt".name[0].family
/*define "21.ParentGuardian.Name: 'TBD'
define "22.ParentGuardian.Phone: 'TBD'
define "23.ParentGuardian.Address”: 'TBD'*/
define "24.Patient.Street_Address": "Pt".address[0]
define "25.Patient.Birth_Date":   "Pt".birthDate[0].value
define "26.Patient.Sex": "Pt".gender[0].value
/*define "27.Patient.Class": 'TBD'
define "28.Patient.Race": 'TBD'
define "29.Patient.Ethnicity":	'TBD'*/
define "30.Patient.Preferred_Language":  "Pt".communication.language
define "31.Patient.Occupation": 'TBD'
define "32.Patient.Pregnancy": 'TBD'
define "33.Patient.Travel_History": 'TBD'
define "34.Patient.Insurance_Type": 'TBD'
define "35.Immunization.Immunization_History": [Immunization] Immunes return Immunes

define "38.Condition.Date_Of_Onset": "Chlamydia Diagnosis".onset.value
define "39.Observation.Symptoms": [Condition: Code in "Chlamydia Symptoms"]
define "40.Lab?.LabOrderCode": 'TBD'
define "41.Provider.Placer_Order_Number": 'TBD'
define "42.Condition.Diagnosis": "Chlamydia Diagnosis"
define "AllEncounters": [Encounter] Encounters
define "FirstEncounter": First("AllEncounters" AllEns)
define "LastEncounter": Last("AllEncounters" AllEns)
define "43.Encounters.Date_Of_Diagnosis": "FirstEncounter".period.start.value
define "44.Medication_Request.MedicationProvided.1": [MedicationRequest:"Macrolides"]
define "44.Medication_Request.MedicationProvided.2": [MedicationRequest:"Levofloxacin"]
define "44.Medication_Request.MedicationProvided.3": [MedicationRequest:"Doxycycline"]
define "45.Patient.Death_Date": "Pt".deceased
define "46.Encounter.Date_Discharged": "LastEncounter".period.end.value
define "48.Condition.Trigger_Code": "Chlamydia Diagnosis".code