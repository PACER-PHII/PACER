package gatech.edu.STIECR.JSON;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Provider implements Comparable<Provider>{
	private static final Logger log = LoggerFactory.getLogger(Provider.class);
	
	@JsonProperty("ID")
	private TypeableID id;
	@JsonProperty("Name")
	private String name = "";
	@JsonProperty("Phone")
	private String phone = "";
	@JsonProperty("Fax")
	private String fax = "";
	@JsonProperty("Email")
	private String email = "";
	@JsonProperty("Facility")
	private String facility = "";
	@JsonProperty("Address")
	private String address = "";
	@JsonProperty("Country")
	private String country = "";

	public Provider() {}
	
	public TypeableID getid() {
		return id;
	}

	public void setid(TypeableID id) {
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

	public String getfax() {
		return fax;
	}

	public void setfax(String fax) {
		this.fax = fax;
	}

	public String getemail() {
		return email;
	}

	public void setemail(String email) {
		this.email = email;
	}

	public String getfacility() {
		return facility;
	}

	public void setfacility(String facility) {
		this.facility = facility;
	}

	public String getaddress() {
		return address;
	}

	public void setaddress(String address) {
		this.address = address;
	}

	public String getcountry() {
		return country;
	}

	public void setcountry(String country) {
		this.country = country;
	}
	
	public void update(Provider newProvider) {
		log.debug("ECR PROVIDER --- this:"+this.toString());
		log.debug("ECR PROVIDER --- newProvider:"+newProvider.toString());
		if(newProvider.getid() != null) {
			log.debug("ECR PROVIDER --- updating id");
			this.id = newProvider.getid();
		}
		if(newProvider.getname() != null) {
			log.debug("ECR PROVIDER --- updating name");
			this.name = newProvider.getname();
		}
		if(newProvider.getphone() != null) {
			log.debug("ECR PROVIDER --- updating phone");
			this.phone = newProvider.getphone();
		}
		if(newProvider.getfax() != null) {
			log.debug("ECR PROVIDER --- updating fax");
			this.fax = newProvider.getfax();
		}
		if(newProvider.getemail() != null) {
			log.debug("ECR PROVIDER --- updating email");
			this.email = newProvider.getemail();
		}
		if(newProvider.getfacility() != null) {
			log.debug("ECR PROVIDER --- updating facility");
			this.facility = newProvider.getfacility();
		}
		if(newProvider.getaddress() != null) {
			log.debug("ECR PROVIDER --- updating address");
			this.address = newProvider.getaddress();
		}
		if(newProvider.getcountry() != null) {
			log.debug("ECR PROVIDER --- updating country");
			this.name = newProvider.getcountry();
			this.country = newProvider.getcountry();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((facility == null) ? 0 : facility.hashCode());
		result = prime * result + ((fax == null) ? 0 : fax.hashCode());
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
		if (!(obj instanceof Provider))
			return false;
		Provider other = (Provider) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (facility == null) {
			if (other.facility != null)
				return false;
		} else if (!facility.equals(other.facility))
			return false;
		if (fax == null) {
			if (other.fax != null)
				return false;
		} else if (!fax.equals(other.fax))
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
		return "Provider [id=" + id + ", name=" + name + ", phone=" + phone + ", fax=" + fax + ", email=" + email
				+ ", facility=" + facility + ", address=" + address + ", country=" + country + "]";
	}

	@Override
	public int compareTo(Provider o) {
		return this.name.compareTo(o.getname());
	}
}
