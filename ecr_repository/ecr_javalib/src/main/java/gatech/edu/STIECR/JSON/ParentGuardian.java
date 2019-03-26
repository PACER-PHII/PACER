package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ParentGuardian implements Comparable<ParentGuardian>{
	@JsonProperty("Name")
	private Name name = new Name();
	@JsonProperty("Phone")
	private String phone = "";
	@JsonProperty("Email")
	private String email = "";

	public Name getname() {
		return name;
	}

	public void setname(Name name) {
		this.name = name;
	}

	public String getphone() {
		return phone;
	}

	public void setphone(String phone) {
		this.phone = phone;
	}

	public String getemail() {
		return email;
	}

	public void setemail(String email) {
		this.email = email;
	}

	@Override
	public int compareTo(ParentGuardian o) {
		if(!this.name.equals(o.getname()))
			return this.name.compareTo(o.getname());
		if(!this.phone.equals(o.getphone()))
			return this.phone.compareTo(o.getphone());
		if(!this.email.equals(o.getemail()))
			return this.email.compareTo(o.getemail());
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((phone == null) ? 0 : phone.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ParentGuardian))
			return false;
		ParentGuardian other = (ParentGuardian) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ParentGuardian [name=" + name + ", phone=" + phone + ", email=" + email + "]";
	}
	
}
