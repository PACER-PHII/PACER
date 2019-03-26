package gatech.edu.STIECR.DB.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.Facility;
import gatech.edu.STIECR.JSON.Name;
import gatech.edu.STIECR.JSON.ParentGuardian;
import gatech.edu.STIECR.JSON.Patient;
import gatech.edu.STIECR.JSON.Provider;
import gatech.edu.STIECR.JSON.utils.AddressUtil;
import gatech.edu.STIECR.JSON.utils.ECRJsonConverter;

@Entity
@Table(name = "ecr_data", schema = "ecr")
public class ECRData {
	@Id
	@Column(name = "case_report_key")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "case_data")
	@Convert(converter = ECRJsonConverter.class)
	private ECR data;
	@Column(name = "case_report_id")
	private Integer ecrId;
	@Column(name = "version")
	private Integer version;
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created_date;
	@Column(name = "last_updated")
	@Temporal(TemporalType.TIMESTAMP)
	private Date last_updated;
	@Column(name = "last_name")
	private String lastName;
	@Column(name = "first_name")
	private String firstName;
	@Column(name = "zip_code")
	private String zipCode;
	@Column(name = "diagnosis")
	private String diagnosisCode;
	@Column(name = "first_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date firstDate;
	@Column(name = "last_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastDate;
	
	public ECRData() {}
	
	public ECRData(ECR ecr, int id) {
		ecr.setId(Integer.toString(id));
		data = ecr;
		ecrId = id;
		version = 1;
		Name patientName = ecr.getPatient().getname();
		if(patientName != null) {
			if(!patientName.getfamily().isEmpty())
				lastName = patientName.getfamily();
			if(!patientName.getgiven().isEmpty())
				firstName = patientName.getgiven();
		}
		zipCode = AddressUtil.findZip(ecr.getPatient().getstreetAddress());
		diagnosisCode = ecr.getPatient().getDiagnosis().get(0).getCode();
		created_date = new Date();
		last_updated = new Date();
	}
	
	public ECRData(ECRData oldData) {
		data = oldData.getECR();
		ecrId = oldData.getECRId();
		version = oldData.getVersion();
		lastName = oldData.getLastName();
		firstName = oldData.getFirstName();
		zipCode = oldData.getZipCode();
		diagnosisCode = oldData.getDiagnosisCode();
		created_date = oldData.getCreated_date();
		last_updated = oldData.getLast_updated();
	}
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public ECR getECR() {
		return data;
	}
	
	public void setECR(ECR ecr) {
		this.data = ecr;
	}
	
	public Integer getECRId() {
		return ecrId;
	}
	
	public void setECRId(Integer ecrId) {
		this.ecrId = ecrId;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public Date getCreated_date() {
		return created_date;
	}

	public void setCreated_date(Date created_date) {
		this.created_date = created_date;
	}

	public Date getLast_updated() {
		return last_updated;
	}

	public void setLast_updated(Date last_updated) {
		this.last_updated = last_updated;
	}
	
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getDiagnosisCode() {
		return diagnosisCode;
	}

	public void setDiagnosisCode(String diagnosisCode) {
		this.diagnosisCode = diagnosisCode;
	}

	public Date getFirstDate() {
		return firstDate;
	}

	public void setFirstDate(Date firstDate) {
		this.firstDate = firstDate;
	}

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public void update(ECR ecr) {
		this.version = this.version + 1;
		this.last_updated = new Date();
		this.data.update(ecr);
	}
}