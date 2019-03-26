package gatech.edu.STIECR.JSON;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LabOrderCode extends CodeableConcept{
	@JsonProperty("Laboratory_Results")
	List<LabResult> laboratory_Results;
	@JsonProperty("Facility")
	protected Facility facility = new Facility();
	@JsonProperty("Provider")
	protected Provider provider = new Provider();
	
	public LabOrderCode() {
		super();
		laboratory_Results = new ArrayList<LabResult>();
	}

	public List<LabResult> getLaboratory_Results() {
		return laboratory_Results;
	}

	public void setLaboratory_Results(List<LabResult> laboratory_Results) {
		this.laboratory_Results = laboratory_Results;
	}
	
	public Facility getFacility() {
		return facility;
	}
	
	public void setFacility(Facility facility) {
		this.facility = facility;
	}
	
	public Provider getProvider() {
		return provider;
	}
	public void setProvider(Provider provider) {
		this.provider = provider;
	}
	
	
	@Override
	public int compareTo(CodeableConcept o) {
		if(o instanceof LabOrderCode) {
			LabOrderCode loc = (LabOrderCode) o;
			for(LabResult labResult : this.laboratory_Results) {
				if(!loc.getLaboratory_Results().contains(labResult)) {
					return -1;
				}
			}
		}
		return super.compareTo(o);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((laboratory_Results == null) ? 0 : laboratory_Results.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (!(obj instanceof LabOrderCode))
			return false;
		LabOrderCode other = (LabOrderCode) obj;
		if (laboratory_Results == null) {
			if (other.laboratory_Results != null)
				return false;
		} else if (!laboratory_Results.equals(other.laboratory_Results))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "LabOrderCode [laboratory_Results=" + laboratory_Results + "]";
	}
	
}
