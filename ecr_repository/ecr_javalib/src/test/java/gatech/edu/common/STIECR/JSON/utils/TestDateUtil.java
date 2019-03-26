/**
 * 
 */
package gatech.edu.common.STIECR.JSON.utils;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import gatech.edu.STIECR.JSON.utils.DateUtil;

/**
 * @author taylorde
 *
 */
public class TestDateUtil {

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#stringToCalendar(java.lang.String)}.
	 */
	@Test
	public void testStringToCalendar() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#stringToDate(java.lang.String)}.
	 */
	@Test
	public void testStringToDate() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#dateToStdString(java.util.Date)}.
	 */
	@Test
	public void testDateToStdString() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#birthdayStringToCalendar(java.lang.String)}.
	 */
	@Test
	public void testBirthdayStringToCalendar() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#DateTimeToStdString(java.util.Date)}.
	 */
	@Test
	public void testDateTimeToStdString() {
		// fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.JSON.utils.DateUtil#stringToDateLong(java.lang.String)}.
	 * @throws ParseException 
	 */
	@Test
	public void testStringToDateLong() throws ParseException {
		// fail("Not yet implemented");
		Date newDate = DateUtil.parse("Feb 17, 2002 12:00:00 AM");
		
		assertNotNull(newDate);
	}
}
