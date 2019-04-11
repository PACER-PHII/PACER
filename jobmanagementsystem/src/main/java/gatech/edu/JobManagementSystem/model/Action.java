package gatech.edu.JobManagementSystem.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@Entity
@Table(name = "action")
public class Action implements Runnable{
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;
	@Column(name = "name")
	protected String name;
	@OneToOne(fetch = FetchType.LAZY)
	@JsonIgnore
	protected PersonList personList;
	@Column(name = "actiontype")
	protected ActionType actionType;
	@Column(name = "cronstring")
	protected String cronString;
	@Column(name = "params")
	@ElementCollection(fetch = FetchType.EAGER)
	protected Map<String,String> params = new HashMap<String,String>();
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public PersonList getPersonList() {
		return personList;
	}
	public void setPersonList(PersonList personList) {
		this.personList = personList;
	}
	public String getCronString() {
		return cronString;
	}
	public void setCronString(String cronString) {
		this.cronString = cronString;
	}
	public ActionType getActionType() {
		return actionType;
	}
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}
	public Map<String, String> getParams() {
		return params;
	}
	
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	
	public void addParam(String key, String value) {
		this.params.put(key, value);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String toString() {
		return "Action [id=" + id + ", name=" + name + ", actionType=" + actionType
				+ ", cronString=" + cronString + ", params=" + params + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actionType == null) ? 0 : actionType.hashCode());
		result = prime * result + ((cronString == null) ? 0 : cronString.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((params == null) ? 0 : params.hashCode());
		result = prime * result + ((personList == null) ? 0 : personList.hashCode());
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
		Action other = (Action) obj;
		if (actionType != other.actionType)
			return false;
		if (cronString == null) {
			if (other.cronString != null)
				return false;
		} else if (!cronString.equals(other.cronString))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (params == null) {
			if (other.params != null)
				return false;
		} else if (!params.equals(other.params))
			return false;
		if (personList == null) {
			if (other.personList != null)
				return false;
		} else if (!personList.equals(other.personList))
			return false;
		return true;
	}
	
	
}
