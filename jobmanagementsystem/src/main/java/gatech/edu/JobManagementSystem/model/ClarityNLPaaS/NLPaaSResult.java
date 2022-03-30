package gatech.edu.JobManagementSystem.model.ClarityNLPaaS;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.databind.JsonNode;

public class NLPaaSResult {
	
	public String _id;
	public String batch;
	public String concept_code;
	public String concept_code_system;
	public String cql_feature;
	public String display_name;
	public String id;
	public String inserted_date;
	public String job_id;
	public String location;
	public String name;
	public String nlpql_feature;
	public String owner;
	public String phenotype_final;
	public String pipeline_id;
	public String pipeline_type;
	public String report_date;
	public String report_id;
	public String report_text;
	public String report_type;
	public String resourceType;
	public Object result;
	public String resultType;
	public JsonNode result_display;
	public String solr_id;
	public String source;
	public String subject;
	public List<NLPaaSTuple> tuples = new ArrayList<NLPaaSTuple>();
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getConcept_code() {
		return concept_code;
	}
	public void setConcept_code(String concept_code) {
		this.concept_code = concept_code;
	}
	public String getConcept_code_system() {
		return concept_code_system;
	}
	public void setConcept_code_system(String concept_code_system) {
		this.concept_code_system = concept_code_system;
	}
	public String getCql_feature() {
		return cql_feature;
	}
	public void setCql_feature(String cql_feature) {
		this.cql_feature = cql_feature;
	}
	public String getDisplay_name() {
		return display_name;
	}
	public void setDisplay_name(String display_name) {
		this.display_name = display_name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInserted_date() {
		return inserted_date;
	}
	public void setInserted_date(String inserted_date) {
		this.inserted_date = inserted_date;
	}
	public String getJob_id() {
		return job_id;
	}
	public void setJob_id(String job_id) {
		this.job_id = job_id;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNlpql_feature() {
		return nlpql_feature;
	}
	public void setNlpql_feature(String nlpql_feature) {
		this.nlpql_feature = nlpql_feature;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getPhenotype_final() {
		return phenotype_final;
	}
	public void setPhenotype_final(String phenotype_final) {
		this.phenotype_final = phenotype_final;
	}
	public String getPipeline_id() {
		return pipeline_id;
	}
	public void setPipeline_id(String pipeline_id) {
		this.pipeline_id = pipeline_id;
	}
	public String getPipeline_type() {
		return pipeline_type;
	}
	public void setPipeline_type(String pipeline_type) {
		this.pipeline_type = pipeline_type;
	}
	public String getReport_date() {
		return report_date;
	}
	public void setReport_date(String report_date) {
		this.report_date = report_date;
	}
	public String getReport_id() {
		return report_id;
	}
	public void setReport_id(String report_id) {
		this.report_id = report_id;
	}
	public String getReport_text() {
		return report_text;
	}
	public void setReport_text(String report_text) {
		this.report_text = report_text;
	}
	public String getReport_type() {
		return report_type;
	}
	public void setReport_type(String report_type) {
		this.report_type = report_type;
	}
	public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public Object getResult() {
		return result;
	}
	public void setResult(Object result) {
		this.result = result;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	public JsonNode getResult_display() {
		return result_display;
	}
	public void setResult_display(JsonNode result_display) {
		this.result_display = result_display;
	}
	public String getSolr_id() {
		return solr_id;
	}
	public void setSolr_id(String solr_id) {
		this.solr_id = solr_id;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public List<NLPaaSTuple> getTuples() {
		return tuples;
	}

	public void setTuples(List<NLPaaSTuple> tuples) {
		this.tuples = tuples;
	}

	@JsonAnySetter
    public void setATuple(String name, String result) {
		if(name.startsWith("tuple")) {
			NLPaaSTuple aTuple = new NLPaaSTuple(result);
			tuples.add(aTuple);
		}
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((batch == null) ? 0 : batch.hashCode());
		result = prime * result + ((concept_code == null) ? 0 : concept_code.hashCode());
		result = prime * result + ((concept_code_system == null) ? 0 : concept_code_system.hashCode());
		result = prime * result + ((cql_feature == null) ? 0 : cql_feature.hashCode());
		result = prime * result + ((display_name == null) ? 0 : display_name.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inserted_date == null) ? 0 : inserted_date.hashCode());
		result = prime * result + ((job_id == null) ? 0 : job_id.hashCode());
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((nlpql_feature == null) ? 0 : nlpql_feature.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((phenotype_final == null) ? 0 : phenotype_final.hashCode());
		result = prime * result + ((pipeline_id == null) ? 0 : pipeline_id.hashCode());
		result = prime * result + ((pipeline_type == null) ? 0 : pipeline_type.hashCode());
		result = prime * result + ((report_date == null) ? 0 : report_date.hashCode());
		result = prime * result + ((report_id == null) ? 0 : report_id.hashCode());
		result = prime * result + ((report_text == null) ? 0 : report_text.hashCode());
		result = prime * result + ((report_type == null) ? 0 : report_type.hashCode());
		result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
		result = prime * result + ((result_display == null) ? 0 : result_display.hashCode());
		result = prime * result + ((solr_id == null) ? 0 : solr_id.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((tuples == null) ? 0 : tuples.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NLPaaSResult other = (NLPaaSResult) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (batch == null) {
			if (other.batch != null)
				return false;
		} else if (!batch.equals(other.batch))
			return false;
		if (concept_code == null) {
			if (other.concept_code != null)
				return false;
		} else if (!concept_code.equals(other.concept_code))
			return false;
		if (concept_code_system == null) {
			if (other.concept_code_system != null)
				return false;
		} else if (!concept_code_system.equals(other.concept_code_system))
			return false;
		if (cql_feature == null) {
			if (other.cql_feature != null)
				return false;
		} else if (!cql_feature.equals(other.cql_feature))
			return false;
		if (display_name == null) {
			if (other.display_name != null)
				return false;
		} else if (!display_name.equals(other.display_name))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inserted_date == null) {
			if (other.inserted_date != null)
				return false;
		} else if (!inserted_date.equals(other.inserted_date))
			return false;
		if (job_id == null) {
			if (other.job_id != null)
				return false;
		} else if (!job_id.equals(other.job_id))
			return false;
		if (location == null) {
			if (other.location != null)
				return false;
		} else if (!location.equals(other.location))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (nlpql_feature == null) {
			if (other.nlpql_feature != null)
				return false;
		} else if (!nlpql_feature.equals(other.nlpql_feature))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (phenotype_final == null) {
			if (other.phenotype_final != null)
				return false;
		} else if (!phenotype_final.equals(other.phenotype_final))
			return false;
		if (pipeline_id == null) {
			if (other.pipeline_id != null)
				return false;
		} else if (!pipeline_id.equals(other.pipeline_id))
			return false;
		if (pipeline_type == null) {
			if (other.pipeline_type != null)
				return false;
		} else if (!pipeline_type.equals(other.pipeline_type))
			return false;
		if (report_date == null) {
			if (other.report_date != null)
				return false;
		} else if (!report_date.equals(other.report_date))
			return false;
		if (report_id == null) {
			if (other.report_id != null)
				return false;
		} else if (!report_id.equals(other.report_id))
			return false;
		if (report_text == null) {
			if (other.report_text != null)
				return false;
		} else if (!report_text.equals(other.report_text))
			return false;
		if (report_type == null) {
			if (other.report_type != null)
				return false;
		} else if (!report_type.equals(other.report_type))
			return false;
		if (resourceType == null) {
			if (other.resourceType != null)
				return false;
		} else if (!resourceType.equals(other.resourceType))
			return false;
		if (result == null) {
			if (other.result != null)
				return false;
		} else if (!result.equals(other.result))
			return false;
		if (resultType == null) {
			if (other.resultType != null)
				return false;
		} else if (!resultType.equals(other.resultType))
			return false;
		if (result_display == null) {
			if (other.result_display != null)
				return false;
		} else if (!result_display.equals(other.result_display))
			return false;
		if (solr_id == null) {
			if (other.solr_id != null)
				return false;
		} else if (!solr_id.equals(other.solr_id))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (tuples == null) {
			if (other.tuples != null)
				return false;
		} else if (!tuples.equals(other.tuples))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "NLPaaSResult [_id=" + _id + ", batch=" + batch + ", concept_code=" + concept_code
				+ ", concept_code_system=" + concept_code_system + ", cql_feature=" + cql_feature + ", display_name="
				+ display_name + ", id=" + id + ", inserted_date=" + inserted_date + ", job_id=" + job_id
				+ ", location=" + location + ", name=" + name + ", nlpql_feature=" + nlpql_feature + ", owner=" + owner
				+ ", phenotype_final=" + phenotype_final + ", pipeline_id=" + pipeline_id + ", pipeline_type="
				+ pipeline_type + ", report_date=" + report_date + ", report_id=" + report_id + ", report_text="
				+ report_text + ", report_type=" + report_type + ", resourceType=" + resourceType + ", result=" + result
				+ ", resultType=" + resultType + ", result_display=" + result_display + ", solr_id=" + solr_id
				+ ", source=" + source + ", subject=" + subject + ", tuples=" + tuples + "]";
	}
}
