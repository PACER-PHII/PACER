package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Facility {
	@JsonProperty("ID")
	private String id;
	@JsonProperty("Name")
	private String name = "";
	@JsonProperty("Phone")
	private String phone = "";
	@JsonProperty("Address")
	private String address = "";
	@JsonProperty("Fax")
	private String fax = "";
	@JsonProperty("Hospital_Unit")
	private String hospitalUnit = "";
	
	public Facility() {};
	public String getid() {
		return id;
	}
	public void setid(String id) {
		this.id = id;
	}
	public String getname() {
		return name;
	}
	public void setname(String name) {
		this.name = name;
	}
	public String getphone() {
		return phone;
	}
	public void setphone(String phone) {
		this.phone = phone;
	}
	public String getaddress() {
		return address;
	}
	public void setaddress(String address) {
		this.address = address;
	}
	public String getfax() {
		return fax;
	}
	public void setfax(String fax) {
		this.fax = fax;
	}
	public String gethospitalUnit() {
		return hospitalUnit;
	}
	public void sethospitalUnit(String hospital_unit) {
		this.hospitalUnit = hospital_unit;
	}
	
	public void update(Facility newFacility) {
		if(newFacility.getname() != null) {
			this.name = newFacility.getname();
		}
		if(newFacility.getphone() != null) {
			this.phone = newFacility.getphone();
		}
		if(newFacility.getaddress() != null) {
			this.address = newFacility.getaddress();
		}
		if(newFacility.getfax() != null) {
			this.fax = newFacility.getfax();
		}
		if(newFacility.gethospitalUnit() != null) {
			this.name = newFacility.gethospitalUnit();
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((fax == null) ? 0 : fax.hashCode());
		result = prime * result + ((hospitalUnit == null) ? 0 : hospitalUnit.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (!(obj instanceof Facility))
			return false;
		Facility other = (Facility) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (fax == null) {
			if (other.fax != null)
				return false;
		} else if (!fax.equals(other.fax))
			return false;
		if (hospitalUnit == null) {
			if (other.hospitalUnit != null)
				return false;
		} else if (!hospitalUnit.equals(other.hospitalUnit))
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
		if (phone == null) {
			if (other.phone != null)
				return false;
		} else if (!phone.equals(other.phone))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Facility [id=" + id + ", name=" + name + ", phone=" + phone + ", address=" + address + ", fax=" + fax
				+ ", hospitalUnit=" + hospitalUnit + "]";
	}
}
