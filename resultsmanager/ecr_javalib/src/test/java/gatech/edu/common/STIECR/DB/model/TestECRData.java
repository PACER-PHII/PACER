/**
 * 
 */
package gatech.edu.common.STIECR.DB.model;

import static org.junit.Assert.*;

import org.junit.Test;

import gatech.edu.STIECR.DB.model.ECRData;
import gatech.edu.STIECR.JSON.ECR;
import gatech.edu.STIECR.JSON.Patient;
import gatech.edu.STIECR.JSON.TypeableID;
import gatech.edu.STIECR.JSON.utils.ECRJsonConverter;

/**
 * @author taylorde
 *
 */
public class TestECRData {

	/**
	 * Test method for {@link gatech.edu.STIECR.DB.model.ECRData#ECRData()}.
	 */
	@Test
	public void testECRData() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.DB.model.ECRData#ECRData(gatech.edu.STIECR.JSON.ECR, int)}.
	 */
	@Test
	public void testECRDataECRInt() {
		ECR ecr = new ECR();
		Patient patient = new Patient();
		TypeableID mrnId = new TypeableID();
		mrnId.settype("MR");
		mrnId.setvalue("1111111");
		patient.getid().add(mrnId);
		ecr.setPatient(patient);
		
		ECRData ecrData = new ECRData(ecr,2222);
		
		ECRJsonConverter converter = new ECRJsonConverter();
		String ecrStr = converter.convertToDatabaseColumn(ecr);
		ECR convertedECR = converter.convertToEntityAttribute(ecrStr);
		
		assertEquals(1, convertedECR.getPatient().getid().size());
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.DB.model.ECRData#getECR()}.
	 */
	@Test
	public void testGetECR() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.DB.model.ECRData#setECR(gatech.edu.STIECR.JSON.ECR)}.
	 */
	@Test
	public void testSetECR() {
//		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link gatech.edu.STIECR.DB.model.ECRData#update(gatech.edu.STIECR.JSON.ECR)}.
	 */
	@Test
	public void testUpdate() {
//		fail("Not yet implemented");
	}
}
