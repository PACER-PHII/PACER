package edu.gatech.chai.fhir.fhirfilter.api;

import edu.gatech.chai.fhir.fhirfilter.dao.FhirFilterDaoImpl;
import edu.gatech.chai.fhir.fhirfilter.model.FilterData;
import edu.gatech.chai.fhir.fhirfilter.model.ListFilterData;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-01-16T14:28:58.456247-05:00[America/New_York]")
@Controller
public class ManageApiController implements ManageApi {

    private static final Logger log = LoggerFactory.getLogger(ManageApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    
	@org.springframework.beans.factory.annotation.Autowired
	FhirFilterDaoImpl fhirFilterDao;

    @org.springframework.beans.factory.annotation.Autowired
    public ManageApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<ListFilterData> manageGet() {
        String accept = request.getHeader("Accept");

        ListFilterData listFilterData = new ListFilterData();
		List<FilterData> filterDatas;
		filterDatas = fhirFilterDao.get();

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String created = dateFormat.format(cal.getTime());
		listFilterData.setCreated(created);
		listFilterData.setCount(filterDatas.size());
		listFilterData.setList(filterDatas);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<ListFilterData>(listFilterData, headers, HttpStatus.OK);
    }

    public ResponseEntity<Void> manageIdDelete(@ApiParam(value = "Profile ID to be deleted.",required=true) @PathVariable("id") Integer id) {
        String accept = request.getHeader("Accept");

        FilterData filterData = fhirFilterDao.getById(id);
		if (filterData == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			fhirFilterDao.delete(id);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			return new ResponseEntity<>(headers, HttpStatus.OK);
		}
    }

    public ResponseEntity<FilterData> manageIdGet(@ApiParam(value = "Profile ID to be read.",required=true) @PathVariable("id") Integer id) {
        String accept = request.getHeader("Accept");

        FilterData filterData = fhirFilterDao.getById(id);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (filterData == null) {
			return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<FilterData>(filterData, headers, HttpStatus.OK);
		}
    }

    public ResponseEntity<Void> manageIdPut(@ApiParam(value = "" ,required=true )  @Valid @RequestBody FilterData body,@ApiParam(value = "Profile ID to be updated.",required=true) @PathVariable("id") Integer id) {
        String accept = request.getHeader("Accept");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		FilterData existingFilterData = fhirFilterDao.getById(id);
		if (existingFilterData == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

		existingFilterData.setProfileName(body.getProfileName());
		existingFilterData.setEntryToRemove(body.getEntryToRemove());
		fhirFilterDao.update(existingFilterData);

		return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    public ResponseEntity<FilterData> manageProfileNameGet(@ApiParam(value = "Profile name to be read.",required=true) @PathVariable("name") String name) {
        String accept = request.getHeader("Accept");

		FilterData filterData = fhirFilterDao.getByName(name);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		if (filterData == null) {
			return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<FilterData>(filterData, headers, HttpStatus.OK);
		}
    }

    public ResponseEntity<Void> managePost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody FilterData body) {
        String accept = request.getHeader("Accept");

		// Check if the profile exists.
		FilterData existingFilterData = fhirFilterDao.getByName(body.getProfileName());
		if (existingFilterData != null) {
			// This is error.
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

		if (body.getId() != null && body.getId() > 0 ) {
			existingFilterData = fhirFilterDao.getById(body.getId());
			if (existingFilterData != null) {
				existingFilterData.setProfileName(body.getProfileName());
				existingFilterData.setEntryToRemove(body.getEntryToRemove());
				fhirFilterDao.update(existingFilterData);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				return new ResponseEntity<>(headers, HttpStatus.OK);
			}
		}
		int id = fhirFilterDao.save(body);

		if (id > 0) {
			URI location = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/manage/{id}").build()
					.expand(id).toUri();
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(location);

			return new ResponseEntity<>(headers, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
