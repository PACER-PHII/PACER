package gatech.edu.JobManagementSystem.model.ClarityNLPaaS;

import java.util.HashMap;
import java.util.Map;

public class NLPaaSTuple {
	private String tupleString;
	public Map<String,String> dictionary = new HashMap();
	
	public NLPaaSTuple(String inputString) {
		this.tupleString = inputString;
		parseMapFromString();
	}

	public String getTupleString() {
		return tupleString;
	}

	public void setTupleString(String tupleString) {
		this.tupleString = tupleString;
	}
	
	public Map<String, String> getDictionary() {
		return dictionary;
	}

	public void setDictionary(Map<String, String> dictionary) {
		this.dictionary = dictionary;
	}

	private void parseMapFromString() {
		String[] keyPairs = this.tupleString.split("\\n\\t");
		for(String keyPair:keyPairs) {
			String[] keyAndValue = keyPair.split("->");
			if(keyAndValue.length >= 2) {
				String key = keyAndValue[0].replaceAll("\\s", "");
				String value = keyAndValue[1].substring(1); //Get rid of leading space
				if(!key.equalsIgnoreCase("null") && !value.equalsIgnoreCase("null")) {
					dictionary.put(key, value);
				}
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tupleString == null) ? 0 : tupleString.hashCode());
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
		NLPaaSTuple other = (NLPaaSTuple) obj;
		if (tupleString == null) {
			if (other.tupleString != null)
				return false;
		} else if (!tupleString.equals(other.tupleString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "NLPaaSTuple [tupleString=" + tupleString + "]";
	}
	
}
