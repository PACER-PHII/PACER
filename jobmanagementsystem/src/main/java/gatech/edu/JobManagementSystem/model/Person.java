package gatech.edu.JobManagementSystem.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "person")
public class Person{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	@Column(name = "referenceId")
	private String referenceId;
	@Column(name = "name")
	private String name;
	@Column(name = "processState")
	private PersonProcessState processState = PersonProcessState.NONE;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JsonIgnore
	private PersonList personList;
	@Column(name = "result")
	@Lob
	private String result;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PersonProcessState getProcessState() {
		return processState;
	}
	public void setProcessState(PersonProcessState processState) {
		this.processState = processState;
	}
	public PersonList getPersonList() {
		return personList;
	}
	public void setPersonList(PersonList personList) {
		this.personList = personList;
	}
	
	public String getFirstName() {
		if(name.split("\\w+").length>1 && name.lastIndexOf(' ') != -1) {
			return name.substring(0, name.lastIndexOf(' '));
		}
		return name;
	}
	
	public String getLastName() {
		if(name.split("\\w+").length>1 && name.lastIndexOf(' ') != -1) {
			return name.substring(name.lastIndexOf(' ') + 1);
		}
		return new String();
	}
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((referenceId == null) ? 0 : referenceId.hashCode());
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
		Person other = (Person) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (referenceId == null) {
			if (other.referenceId != null)
				return false;
		} else if (!referenceId.equals(other.referenceId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Person [id=" + id + ", referenceId=" + referenceId + ", name=" + name + ", processState=" + processState
				+ ", result=" + result + "]";
	}
}
