package gatech.edu.STIECR.JSON;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Name implements Comparable<Name>{
	@JsonProperty("given")
	private String given = "";
	@JsonProperty("family")
	private String family = "";

	public Name() {}
	
	public Name(String given, String family) {
		this.given = given;
		this.family = family;
	}
	
	/**
	 * 
	 * @param name - Fully qualified name in the form "family`delimiter`given
	 * @param delimiter The character seperating the family and given form
	 */
	public Name(String name, char delimiter) {
		String[] split = name.split(Character.toString(delimiter));
		family = split[0];
		if(split.length > 1)
			given = split[1];
	}

	public String getgiven() {
		return given;
	}

	public void setgiven(String given) {
		this.given = given;
	}

	public String getfamily() {
		return family;
	}

	public void setfamily(String family) {
		this.family = family;
	}
	
	public String toString() {
		return family + "," + given;
	}

	@Override
	public int compareTo(Name o) {
		if(o.family.equals(this.family)) {
			return o.given.compareTo(this.given);
		}
		return o.family.compareTo(this.family);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((family == null) ? 0 : family.hashCode());
		result = prime * result + ((given == null) ? 0 : given.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Name))
			return false;
		Name other = (Name) obj;
		if (family == null) {
			if (other.family != null)
				return false;
		} else if (!family.equals(other.family))
			return false;
		if (given == null) {
			if (other.given != null)
				return false;
		} else if (!given.equals(other.given))
			return false;
		return true;
	}
	
}