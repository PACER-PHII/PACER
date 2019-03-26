package edu.gatech.ResultsManager.FHIR2ECR.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.hl7.fhir.dstu3.model.DateTimeType;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.AgeDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.RangeDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;

public class HAPIFHIRUtil {
	//DSTU2
	public static Date getDate(IDatatype data) {
		if(data instanceof DateTimeDt) {
			return getDate((DateTimeDt)data);
		}
		if(data instanceof AgeDt) {
			return getDate((AgeDt)data);
		}
		if(data instanceof PeriodDt) {
			return getDate((PeriodDt)data);
		}
		if(data instanceof RangeDt) {
			return getDate((RangeDt)data);
		}
		if(data instanceof StringDt) {
			return getDate((StringDt)data);
		}
		return null;
	}
	
	public static Date getDate(DateTimeDt dateTime) {
		return dateTime.getValue();
	}
	public static Date getDate(AgeDt age) {
		return null;
	}
	public static Date getDate(PeriodDt period) {
		return period.getStart();
	}
	public static Date getDate(RangeDt range) {
		return null;
	}
	public static Date getDate(StringDt string) {
		try {
			return DateFormat.getDateInstance().parse(string.toString());
		} catch (ParseException e) {
			return null;
		}
	}
	
	//STU3
	
	public static Date getDate(DateTimeType dateTimeType) {
		return new Date(dateTimeType.getValue().getTime());
	}
	
	public static String addressToString(AddressDt address) {
		String returnString = "";
		for(StringDt line: address.getLine()) {
			returnString.concat(line.getValue());
			returnString.concat(" ");
		}
		returnString.concat(address.getCity());
		returnString.concat(", ");
		returnString.concat(address.getState());
		returnString.concat(" ");
		returnString.concat(address.getPostalCode());
		return returnString;
	}
	
	public static AddressDt stringToAddress(String string) {
		AddressDt returnAddress = new AddressDt();
		int lineEndIndex = string.indexOf(", ");
		returnAddress.addLine(string.substring(0, lineEndIndex));
		string = string.substring(lineEndIndex+2);
		int stateEndIndex = string.indexOf(" ");
		returnAddress.setState(string.substring(0,stateEndIndex));
		string = string.substring(stateEndIndex+1);
		returnAddress.setPostalCode(string); //End of formatted string
		return returnAddress;
	}
	
	public static String getIdFromFullURL(String URL) {
		return URL.substring(URL.lastIndexOf('/') + 1);
	}
}
