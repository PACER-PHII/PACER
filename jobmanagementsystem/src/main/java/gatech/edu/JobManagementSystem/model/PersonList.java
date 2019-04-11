package gatech.edu.JobManagementSystem.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "personList")
public class PersonList {
	
	@Id
	@Column(name = "name",unique = true)
	private String name;
	@Column(name = "jobType")
	private String jobType;
	@Column(name = "listType")
	private ListType listType = ListType.SINGLE_USE;
	@Column(name = "runType")
	private ListRunType runType = ListRunType.ALL;
	@Column(name = "listElements")
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="personList")
	private Set<Person> listElements;
	@OneToOne(mappedBy = "personList", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Action action;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJobType() {
		return jobType;
	}
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	public ListType getListType() {
		return listType;
	}
	public void setListType(ListType type) {
		this.listType = type;
	}
	public ListRunType getRunType() {
		return runType;
	}
	public void setRunType(ListRunType runType) {
		this.runType = runType;
	}
	public Set<Person> getListElements() {
		return listElements;
	}
	public void setListElements(Set<Person> listElements) {
		this.listElements = listElements;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	
	@JsonIgnore
	public Set<Person> getRunnableList(){
		Set<Person> returnSet = listElements;
		switch(runType) {
		case NEW_ONLY:
			returnSet = returnSet.stream().filter(x -> x.getProcessState().equals(PersonProcessState.NONE)
					|| x.getProcessState().equals(PersonProcessState.INLIST)
					|| x.getProcessState().equals(PersonProcessState.ERROR)).collect(Collectors.toSet());
			break;
		default:
		}
		return returnSet;
	}
	
	@Override
	public String toString() {
		return "PersonList [ name=" + name + ", jobType=" + jobType + ", listType=" + listType
				+ ", runType=" + runType + ", listElements=" + listElements + ", action=" + action + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((jobType == null) ? 0 : jobType.hashCode());
		result = prime * result + ((listElements == null) ? 0 : listElements.hashCode());
		result = prime * result + ((listType == null) ? 0 : listType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((runType == null) ? 0 : runType.hashCode());
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
		PersonList other = (PersonList) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (jobType == null) {
			if (other.jobType != null)
				return false;
		} else if (!jobType.equals(other.jobType))
			return false;
		if (listElements == null) {
			if (other.listElements != null)
				return false;
		} else if (!listElements.equals(other.listElements))
			return false;
		if (listType != other.listType)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (runType != other.runType)
			return false;
		return true;
	}
	
	
	
	
}