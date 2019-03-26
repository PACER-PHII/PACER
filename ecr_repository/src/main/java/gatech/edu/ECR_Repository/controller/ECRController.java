package gatech.edu.ECR_Repository.controller;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import gatech.edu.ECR_Repository.ConnectionConfiguration;
import gatech.edu.STIECR.DB.model.ECRData;
import gatech.edu.STIECR.DB.repo.ECRDataRepository;
import gatech.edu.STIECR.JSON.ECR;

@CrossOrigin
@RestController
public class ECRController {
	
	private static final Logger log = LoggerFactory.getLogger(ECRController.class);
	private static final Integer PAGE_SIZE = 50;
	
	private static final String GET_CASE_REPORT_SEQ = "select coalesce(MAX(case_report_id),0) as seq_id from ecr.ecr_data;";
	@Autowired protected ConnectionConfiguration connectionConfig;
	protected final ECRDataRepository ecrDataRepository;
	private static AtomicInteger currentId;
	
	@Autowired
	public ECRController(ECRDataRepository ecrDataRepository) {
		this.ecrDataRepository = ecrDataRepository;
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.POST)
	public ResponseEntity<ECR> postNewECR(@RequestBody ECR ecr){
		ECRData data = new ECRData(ecr,currentId.incrementAndGet());
		ecrDataRepository.save(data);
		return new ResponseEntity<ECR>(data.getECR(),HttpStatus.CREATED);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET)
	public ResponseEntity<List<ECR> > getECR(@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findAll(pageable).getContent();
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = "id")
	public ResponseEntity<ECR> getECRByECRId(@RequestParam Integer id){
		ECRData data = ecrDataRepository.findByEcrIdOrderByVersionDesc(id.toString()).get(0);
		return new ResponseEntity<ECR>(data.getECR(),HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = "lastName")
	public ResponseEntity<List<ECR> > getECRByLastName(@RequestParam String lastName,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameOrderByVersionDesc(lastName,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = "firstName")
	public ResponseEntity<List<ECR> > getECRByFirstName(@RequestParam String firstName,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByFirstNameOrderByVersionDesc(firstName,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = "zipCode")
	public ResponseEntity<List<ECR> > getECRByZipCode(@RequestParam String zipCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByZipCodeOrderByVersionDesc(zipCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = "diagnosisCode")
	public ResponseEntity<List<ECR> > getECRByDiagnosisCode(@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByDiagnosisCodeOrderByVersionDesc(diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","firstName"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndFirstName(@RequestParam String lastName,@RequestParam String firstName,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndFirstNameOrderByVersionDesc(lastName,firstName,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","zipCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndZipCode(@RequestParam String lastName,@RequestParam String zipCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndZipCodeOrderByVersionDesc(lastName,zipCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndDiagnosisCode(@RequestParam String lastName,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndDiagnosisCodeOrderByVersionDesc(lastName,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"firstName","zipCode"})
	public ResponseEntity<List<ECR> > getECRByFirstNameAndZipCode(@RequestParam String firstName,@RequestParam String zipCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByFirstNameAndZipCodeOrderByVersionDesc(firstName,zipCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"firstName","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByFirstNameAndDiagnosisCode(@RequestParam String firstName,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByFirstNameAndDiagnosisCodeOrderByVersionDesc(firstName,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"zipCode","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByZipCodeAndDiagnosisCode(@RequestParam String zipCode,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByZipCodeAndDiagnosisCodeOrderByVersionDesc(zipCode,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","firstName","zipCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndFirstNameAndZipCode(@RequestParam String lastName,@RequestParam String firstName,@RequestParam String zipCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndFirstNameAndZipCodeOrderByVersionDesc(lastName,firstName,zipCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","firstName","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndFirstNameAndDiagnosisCode(@RequestParam String lastName,@RequestParam String firstName,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndFirstNameAndDiagnosisCodeOrderByVersionDesc(lastName,firstName,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"lastName","zipCode","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndZipCodeAndDiagnosisCode(@RequestParam String lastName,@RequestParam String zipCode,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(lastName,zipCode,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"firstName","zipCode","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByFirstNameAndZipCodeAndDiagnosisCode(@RequestParam String firstName,@RequestParam String zipCode,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByFirstNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(firstName,zipCode,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.GET, params = {"LastName","firstName","zipCode","diagnosisCode"})
	public ResponseEntity<List<ECR> > getECRByLastNameAndFirstNameAndZipCodeAndDiagnosisCode(@RequestParam String lastName,@RequestParam String firstName,@RequestParam String zipCode,@RequestParam String diagnosisCode,@RequestParam(name = "page",defaultValue = "0",required = false) Integer page){
		List<ECRData> data = new ArrayList<ECRData>();
		int diffPull = -1;
		while(diffPull != 0 && data.size() < PAGE_SIZE) {
			Pageable pageable = new PageRequest(page,PAGE_SIZE);
			List<ECRData> incomingData = ecrDataRepository.findByLastNameAndFirstNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(lastName,firstName,zipCode,diagnosisCode,pageable);
			int oldSize = data.size();
			data.addAll(incomingData);
			data = lintVersionsFromECRDataList(data);
			diffPull = data.size() - oldSize;
			page = page + 1;
		}
		data = data.size() < PAGE_SIZE ? data.subList(0, data.size()) : data.subList(0, PAGE_SIZE);
		List<ECR> ecrReturnList = transformECRDataToECR(data);
		return new ResponseEntity<List<ECR> >(ecrReturnList,HttpStatus.OK);
	}
	
	@RequestMapping(value = "/ECR", method = RequestMethod.PUT)
	public ResponseEntity<ECR> updateECR(@RequestBody ECR ecr){
		ECRData data = ecrDataRepository.findByEcrIdOrderByVersionDesc(ecr.getECRId()).get(0);
		ECRData updatingData = new ECRData(data);
		updatingData.setId(currentId.incrementAndGet());
		updatingData.update(ecr);
		ecrDataRepository.save(updatingData);
		return new ResponseEntity<ECR>(updatingData.getECR(),HttpStatus.OK);
	}
	
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(connectionConfig.getUrl(), connectionConfig.getUsername(), connectionConfig.getPassword());
	}
	
	@PostConstruct
	public void setCurrentId() {
		log.info(" CONNECTION --- Setting the currentId");
		try {
			currentId = new AtomicInteger(getCurrentId());
		} catch (SQLException e) {
			currentId = new AtomicInteger(1234);
			log.error("Error pulling the currentId");
			log.error(e.getMessage());
		}
	}
	public Integer getCurrentId() throws SQLException{
		log.info(" CONNECTION --- Calling get the currentId");
		log.info(" CONNECTION --- connectionURL:" + connectionConfig.getUrl());
		log.info(" CONNECTION --- connectionUsername:" + connectionConfig.getUsername());
		log.info(" CONNECTION --- connectionPassword:" + connectionConfig.getPassword());
		Connection conn;
		conn = getConnection();
		PreparedStatement runETLStatement = conn.prepareStatement(GET_CASE_REPORT_SEQ);
		ResultSet rs = runETLStatement.executeQuery();
		int returnValue = 0;
		while(rs.next()) {
			 returnValue = rs.getInt("seq_id");
		}
		return returnValue;
	}
	
	//Lints out the old versions of ECRData
	public List<ECRData> lintVersionsFromECRDataList(List<ECRData> sourceList){
		List<ECRData> returnList = new ArrayList<ECRData>(sourceList);
		for(ECRData a: sourceList) {
			for(ECRData b: sourceList) {
				if(a.getECRId().equals(b.getECRId())) {
					if(a.getVersion().compareTo(b.getVersion()) < 0)
						returnList.remove(a);
					else if(a.getVersion().compareTo(b.getVersion()) > 0)
						returnList.remove(b);
				}
			}
		}
		return returnList;
	}
	
	//Transform a list to ECR
	public List<ECR> transformECRDataToECR(List<ECRData> sourceList){
		List<ECR> targetList = new ArrayList<ECR>();
		for(ECRData data:sourceList) {
			targetList.add(data.getECR());
		}
		return targetList;
	}
}
