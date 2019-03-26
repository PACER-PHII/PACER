package gatech.edu.STIECR.JSON.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {
	private static final String defaultDateFormat = "dd-MM-yy";
	private static final String birthdayDateFormat = "yyyyMMdd";
//	private static final String defaultDateTimeFormat = "yyyyMMddhhmmss";
	private static final String fhirDateTimeFormat = "MMM dd, yyyy h:m:s a";
	private static final SimpleDateFormat fhirDateTimeFormmater = new SimpleDateFormat(fhirDateTimeFormat);
	private static final SimpleDateFormat sdFormmatter = new SimpleDateFormat(defaultDateFormat);
	private static final SimpleDateFormat birthdayFormmatter = new SimpleDateFormat(birthdayDateFormat);
	private static final DateFormat sdDateTimeFormmatter = DateFormat.getDateTimeInstance();
	
	public static Calendar stringToCalendar(String string) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdFormmatter.parse(string));
		return cal;
	}
	
	public static Date stringToDate(String string) throws ParseException {
		return sdFormmatter.parse(string);
	}
	
	public static String dateToStdString(Date date) {
		return sdFormmatter.format(date);
	}
	
	public static Calendar birthdayStringToCalendar(String string) throws ParseException{
		Calendar cal = Calendar.getInstance();
		cal.setTime(birthdayFormmatter.parse(string));
		return cal;
	}
	
	public static String dateTimeToStdString(Date dateTime) {
		return sdDateTimeFormmatter.format(dateTime);
	}
	
	
	public static Date dateTimeStringToDateTime(String string) throws ParseException {
		return fhirDateTimeFormmater.parse(string);
	}
	
	public static String stringToDateTime(Date date){
		return fhirDateTimeFormmater.format(date);
	}

	public static Date parse(String dateStr) throws ParseException {
		if ( StringUtils.isBlank(dateStr) ) {
			return null;
		}
		
		if ( dateStr.indexOf("-") > 0 ) {
			return DateUtil.stringToDate(dateStr);
		} else
		if ( StringUtils.isAlpha(dateStr.substring(0, 3)) ) {
			return DateUtil.dateTimeStringToDateTime(dateStr); //Long(onsetDateStr);
		} else {
			return DateUtil.birthdayStringToCalendar(dateStr).getTime();
		}
	}
}
