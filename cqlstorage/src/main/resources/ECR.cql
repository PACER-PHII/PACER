library ChlamydiaECR version '1.0'
using FHIR version '3.0.0'
include FHIRHelpers version '3.0.0' called FHIRHelpers
codesystem "ICD9PROC": 'http://hl7.org/fhir/sid/icd-9-proc'
codesystem "LOINC": 'http://loinc.org'
codesystem "CPT": 'http://www.ama-assn.org/go/cpt'
codesystem "icd10cm": 'http://hl7.org/fhir/sid/icd-10-cm'
codesystem "sct": 'http://snomed.info/sct'
codesystem "rxnorm": 'http://www.nlm.nih.gov/research/umls/rxnorm'
define "Chlamydia_Codes": Concept { Code 'A56.01' from icd10cm, Code 'A56.00' from icd10cm, Code 'A56.09' from icd10cm, Code '179101003' from sct, Code '446752000' from sct, Code '186729003' from sct, Code '367504009' from sct, Code '188463006' from sct, Code '447353001' from sct, Code '237097008' from sct, Code '420910002' from sct, Code '197172005' from sct, Code '240589008' from sct, Code '447386002' from sct, Code '186731007' from sct, Code '447372001' from sct, Code '426165006' from sct, Code '198176005' from sct, Code '312099009' from sct, Code '237084006' from sct, Code '447402003' from sct, Code '236683007' from sct, Code '428015005' from sct, Code '426247003' from sct, Code '237043008' from sct, Code '237106009' from sct, Code '105629000' from sct, Code '446642005' from sct, Code '237039009' from sct, Code '189312004' from sct }
define "Immunization_Codes": Concept { Code '1292459' from rxnorm, Code '1942166' from rxnorm, Code '901644' from rxnorm, Code '1300206' from rxnorm, Code '798300' from rxnorm, Code '1660929' from rxnorm, Code '830245' from rxnorm, Code '207269' from rxnorm, Code '1657236' from rxnorm, Code '1799205' from rxnorm, Code '1301891' from rxnorm, Code '1801163' from rxnorm, Code '864781' from rxnorm, Code '1928537' from rxnorm, Code '807225' from rxnorm, Code '901518' from rxnorm, Code '1607808' from rxnorm, Code '798430' from rxnorm, Code '798278' from rxnorm, Code '1986830' from rxnorm, Code '830470' from rxnorm, Code '1658714' from rxnorm, Code '1942173' from rxnorm, Code '205259' from rxnorm, Code '1928362' from rxnorm, Code '1801611' from rxnorm, Code '1928880' from rxnorm, Code '1661113' from rxnorm, Code '1658155' from rxnorm, Code '805579' from rxnorm, Code '1658157' from rxnorm, Code '1658150' from rxnorm, Code '2109620' from rxnorm, Code '1801174' from rxnorm, Code '2050059' from rxnorm, Code '798428' from rxnorm, Code '1437916' from rxnorm, Code '1300783' from rxnorm, Code '798443' from rxnorm, Code '1946986' from rxnorm, Code '830463' from rxnorm, Code '1099936' from rxnorm, Code '1658228' from rxnorm, Code '1653579' from rxnorm, Code '762602' from rxnorm, Code '1876710' from rxnorm, Code '803367' from rxnorm, Code '1658106' from rxnorm, Code '1658066' from rxnorm, Code '1658102' from rxnorm, Code '1799106' from rxnorm, Code '1928317' from rxnorm, Code '1300386' from rxnorm, Code '1300384' from rxnorm, Code '1658060' from rxnorm, Code '1190919' from rxnorm, Code '798451' from rxnorm, Code '1300310' from rxnorm, Code '864704' from rxnorm, Code '1601407' from rxnorm, Code '1812951' from rxnorm, Code '1942158' from rxnorm, Code '797641' from rxnorm, Code '1190916' from rxnorm, Code '1597099' from rxnorm, Code '1801830' from rxnorm, Code '829989' from rxnorm, Code '1928540' from rxnorm, Code '1801157' from rxnorm, Code '803371' from rxnorm, Code '2050795' from rxnorm, Code '1928307' from rxnorm, Code '1994352' from rxnorm, Code '2050797' from rxnorm, Code '804186' from rxnorm, Code '864797' from rxnorm, Code '2048976' from rxnorm, Code '2048974' from rxnorm, Code '1799123' from rxnorm, Code '1659737' from rxnorm, Code '1547582' from rxnorm, Code '2055034' from rxnorm, Code '1442210' from rxnorm, Code '2055031' from rxnorm, Code '1928332' from rxnorm, Code '1803024' from rxnorm, Code '2050382' from rxnorm, Code '830557' from rxnorm, Code '1799403' from rxnorm, Code '1928336' from rxnorm, Code '1928974' from rxnorm, Code '2050782' from rxnorm, Code '1658241' from rxnorm, Code '2050787' from rxnorm, Code '2050423' from rxnorm, Code '1801082' from rxnorm, Code '2050789' from rxnorm, Code '1099940' from rxnorm, Code '1300378' from rxnorm, Code '1942132' from rxnorm, Code '2050803' from rxnorm, Code '1300810' from rxnorm, Code '763106' from rxnorm, Code '1659729' from rxnorm, Code '830552' from rxnorm, Code '1486496' from rxnorm, Code '1724012' from rxnorm, Code '1657144' from rxnorm, Code '1300370' from rxnorm, Code '2050771' from rxnorm, Code '1928965' from rxnorm, Code '1658472' from rxnorm, Code '805378' from rxnorm, Code '1928329' from rxnorm, Code '2050777' from rxnorm, Code '798482' from rxnorm, Code '1298819' from rxnorm, Code '1292435' from rxnorm, Code '1537521' from rxnorm, Code '1300468' from rxnorm, Code '1657339' from rxnorm, Code '1658706' from rxnorm, Code '1858963' from rxnorm, Code '1858966' from rxnorm, Code '832682' from rxnorm, Code '1597103' from rxnorm, Code '830263' from rxnorm, Code '1593140' from rxnorm, Code '1657330' from rxnorm, Code '1658144' from rxnorm, Code '1801189' from rxnorm, Code '1928356' from rxnorm, Code '1661807' from rxnorm, Code '1801183' from rxnorm, Code '798479' from rxnorm, Code '1367005' from rxnorm, Code '1928359' from rxnorm, Code '1292828' from rxnorm, Code '2050763' from rxnorm, Code '1801186' from rxnorm, Code '2048966' from rxnorm, Code '1292443' from rxnorm, Code '836636' from rxnorm, Code '836634' from rxnorm, Code '1244205' from rxnorm, Code '1659748' from rxnorm, Code '1928470' from rxnorm, Code '830253' from rxnorm, Code '2054274' from rxnorm, Code '1300191' from rxnorm, Code '1928344' from rxnorm, Code '1928982' from rxnorm, Code '1659983' from rxnorm, Code '1799413' from rxnorm, Code '1801075' from rxnorm, Code '1664644' from rxnorm, Code '1661812' from rxnorm, Code '1801193' from rxnorm }
define "Macrolides": Concept { Code '315090' from rxnorm, Code '240741' from rxnorm, Code '686355' from rxnorm, Code '597455' from rxnorm, Code '598006' from rxnorm, Code '204844' from rxnorm, Code '577378' from rxnorm, Code '487129' from rxnorm, Code '310157' from rxnorm, Code '686405' from rxnorm, Code '686406' from rxnorm, Code '686400' from rxnorm, Code '141962' from rxnorm, Code '308460' from rxnorm, Code '141963' from rxnorm, Code '861416' from rxnorm, Code '1668238' from rxnorm, Code '245242' from rxnorm, Code '245240' from rxnorm, Code '1668264' from rxnorm, Code '248656' from rxnorm, Code '310154' from rxnorm, Code '686383' from rxnorm, Code '310155' from rxnorm, Code '197516' from rxnorm, Code '309322' from rxnorm, Code '686418' from rxnorm, Code '359385' from rxnorm, Code '197517' from rxnorm, Code '197650' from rxnorm, Code '308459' from rxnorm }
define "Levofloxacin": Concept { Code '1665507' from rxnorm, Code '1665519' from rxnorm, Code '1665497' from rxnorm, Code '199884' from rxnorm, Code '477391' from rxnorm, Code '311296' from rxnorm, Code '1665515' from rxnorm, Code '1665517' from rxnorm, Code '199885' from rxnorm }
define "Doxycycline": Concept { Code '434018' from rxnorm, Code '348870' from rxnorm, Code '406524' from rxnorm, Code '1649990' from rxnorm, Code '1650143' from rxnorm, Code '1653433' from rxnorm, Code '1650444' from rxnorm, Code '1650446' from rxnorm, Code '1650142' from rxnorm, Code '1791505' from rxnorm, Code '348869' from rxnorm, Code '1998483' from rxnorm, Code '1423080' from rxnorm, Code '1649405' from rxnorm, Code '1649988' from rxnorm, Code '1649425' from rxnorm, Code '757466' from rxnorm, Code '1649401' from rxnorm, Code '799048' from rxnorm, Code '757464' from rxnorm, Code '901399' from rxnorm, Code '1652674' from rxnorm, Code '1652673' from rxnorm, Code '728207' from rxnorm, Code '757460' from rxnorm, Code '835700' from rxnorm, Code '597521' from rxnorm, Code '597520' from rxnorm, Code '700408' from rxnorm, Code '1801138' from rxnorm, Code '310026' from rxnorm, Code '310027' from rxnorm, Code '310028' from rxnorm, Code '1650030' from rxnorm, Code '283535' from rxnorm, Code '1650032' from rxnorm, Code '1649429' from rxnorm }
define "Chlamydia Symptoms": Concept { Code '49650001' from "sct", Code '2910007' from "sct" }

context Patient

define "myFeature": 'myText'
define "Pt": [Patient] Pt
define "Chlamydia Diagnosis": [Condition: Code in "Chlamydia_Codes"]
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
