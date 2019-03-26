package gatech.edu.STIECR.DB.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.TypeableID;

@Entity
@Table(name = "ecr_job", schema = "ecr")
public class ECRJob {
	@Id
	@Column(name = "ecr_job_key")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "case_report_key")
	private Integer reportId;
	@Column(name = "patient_id")
	private Integer patientId;
	@Column(name = "next_run_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date nextRunDate;
	@Column(name = "status_code", length = 3)
	private String statusCode = "I";
	@Column(name = "update_count")
	private Integer updateCount = 0;
	@Column(name = "max_updates")
	private Integer maxUpdates = 4;
	@Column(name = "created_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdDate = new Date();
	@Column(name = "last_update_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdateDate = new Date();

	public ECRJob() {
	}

	public ECRJob(ECR ecr) {
		reportId = Integer.valueOf(ecr.getECRId());
		for(TypeableID id: ecr.getPatient().getid()) {
			try{
				patientId = Integer.parseInt(id.getvalue());
				break;
			}
			catch(NumberFormatException e) {
				continue;
			}
		}
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public Integer getPatientId() {
		return patientId;
	}

	public void setPatientId(Integer patientId) {
		this.patientId = patientId;
	}

	public Date getNextRunDate() {
		return nextRunDate;
	}

	public void setNextRunDate(Date nextRunDate) {
		this.nextRunDate = nextRunDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public Integer getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(Integer updateCount) {
		this.updateCount = updateCount;
	}

	public Integer getMaxUpdates() {
		return maxUpdates;
	}

	public void setMaxUpdates(Integer maxUpdates) {
		this.maxUpdates = maxUpdates;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public void startRun() {
		statusCode = "R";
	}

	public void finishRun() {
		updateCount++;
		if (updateCount >= maxUpdates) {
			statusCode = "C";
		} else {
			statusCode = "A";
		}
		lastUpdateDate = new Date();
	}

	public void cancelJob() {
		statusCode = "I";
	}

	public void instantUpdate() {
		finishRun();
	}
}
