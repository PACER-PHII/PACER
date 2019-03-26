package edu.gatech.chai.fhir.fhirfilter.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.gatech.chai.fhir.fhirfilter.dao.FhirFilterDaoImpl;
import edu.gatech.chai.fhir.fhirfilter.model.FilterData;
import io.swagger.annotations.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-01-16T14:28:58.456247-05:00[America/New_York]")
@Controller
public class ApplyApiController implements ApplyApi {

    private static final Logger log = LoggerFactory.getLogger(ApplyApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
	FhirFilterDaoImpl fhirFilterDao;
    
    @org.springframework.beans.factory.annotation.Autowired
    public ApplyApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<String> applyIdsPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody String body,@ApiParam(value = "Profile IDs to be applied (separated by comma).",required=true) @PathVariable("ids") String ids) {
        String accept = request.getHeader("Accept");

		JSONObject originalJSON = null;
		try {
			originalJSON = new JSONObject(body);
		} catch (JSONException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("Incorrect JSON Format", HttpStatus.BAD_REQUEST);
		}

		// Received JSON should be a FHIR resource.
		if (!originalJSON.has("resourceType")) {
			return new ResponseEntity<>("Invalid FHIR Resource", HttpStatus.BAD_REQUEST);
		}

		List<Integer> idIntList;
		if (ids != null && !ids.isEmpty()) {
			List<String> idStringList = Arrays.asList(ids.split(",")); 
			idIntList = idStringList.stream().map(Integer::valueOf).collect(Collectors.toList());
		} else {
			idIntList = null;
		}
		
		String retv = applyPostProcess(idIntList, originalJSON);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<>(retv, headers, HttpStatus.OK);
    }

    public ResponseEntity<String> applyPost(@ApiParam(value = "" ,required=true )  @Valid @RequestBody String body) {
		JSONObject originalJSON = null;
		try {
			originalJSON = new JSONObject(body);
		} catch (JSONException e) {
			log.error(e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>("{\"error\": \"Incorrect JSON Format\"", HttpStatus.BAD_REQUEST);
		}

		// Received JSON should be a FHIR resource.
		if (!originalJSON.has("resourceType")) {
			return new ResponseEntity<>("{\"error\": \"Invalid FHIR Resource\"", HttpStatus.BAD_REQUEST);
		}

		String retv = applyPostProcess(null, originalJSON);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		return new ResponseEntity<>(retv, headers, HttpStatus.OK);
    }

    private String applyPostProcess(List<Integer> idIntList, JSONObject originalJSON) {
		List<FilterData> filterDataList = new ArrayList<FilterData>();
		if (idIntList == null || idIntList.isEmpty()) {
			filterDataList = fhirFilterDao.get();
		} else {
			for (Integer id : idIntList) {
				// Get filter data
				filterDataList.add(fhirFilterDao.getById(id));
			}
		}

		// Work on orginal data and get only resource part and put them in the list.
		boolean done = false;
		for (FilterData filterData : filterDataList) {
			JSONArray filterEntryJson = new JSONArray(filterData.getEntryToRemove());
			for (int i = 0; i < filterEntryJson.length(); i++) {
				JSONObject filterJson = filterEntryJson.getJSONObject(i);

				if ("Bundle".equals(originalJSON.getString("resourceType"))) {
					JSONArray originalEntry = originalJSON.getJSONArray("entry");
					int deletedCount = 0;
					for (int j = 0; j < originalEntry.length(); j++) {
						JSONObject resourceEntry = originalEntry.getJSONObject(j);
						JSONObject resource = resourceEntry.getJSONObject("resource");
						if (!filterJson.getString("resourceType").equals(resource.getString("resourceType"))) {
							// No match. move on to next entry.
							continue;
						}

						JSONArray filterKeyArray = filterJson.names();
						if (filterKeyArray.length() <= 1) {
							// We have no elements in the filter entry resource.
							// This means that we are removing this resource.
							log.info("Resource level removal: " + resource.getString("resourceType") + "(" + resource.get("id") + ")");
							originalEntry.remove(j--);
							deletedCount++;
							continue;
						}

						if (processJSONObject(resource, filterJson)) {
							log.info("A resource removed: " + resource.getString("resourceType") + "(" + resource.get("id") + ")");
							originalEntry.remove(j--);
							deletedCount++;
						}
					}
					if (originalJSON.has("total")) {
						// Do we need to update the total?? If this is paged bundle, this will reduce
						// the size of pageoffset.
						// We may need further discussion on this.
						int total = originalJSON.getInt("total");
						originalJSON.put("total", total - deletedCount);
					}
					
					if (originalEntry.length() == 0) {
						originalJSON.remove("entry");
						done = true;
						break;
					}

				} else {
					if (processJSONObject(originalJSON, filterJson)) {
						originalJSON = new JSONObject();
						done = true;
						break;
					}
				}
			}
			if (done == true) break;
		}

		return originalJSON.toString();
    }
    
	/***
	 * processJSONObject: process JSON Object with a matching JSON filter object.
	 * 
	 * @return true or false (true to remove. false to leave it. content may be
	 *         changed).
	 */
	private boolean processJSONObject(JSONObject resource, JSONObject filter) {
		JSONArray filterKeyArray = filter.names();

		boolean retv = true;
		for (int i = 0; i < filterKeyArray.length(); i++) {
			boolean replace = false; // set this to true if you want to replace all non matching primitive value.

			String currentFilterKey = (String) filterKeyArray.get(i);
			String currentKey;
			if (currentFilterKey.startsWith("^")) {
				replace = true;
				currentKey = currentFilterKey.substring(1);
			} else {
				currentKey = currentFilterKey;
			}

			if ("resourceType".equalsIgnoreCase(currentKey))
				continue;
			if (!resource.has(currentKey))
				continue; // We don't have matching key.

			// If the object for the currentKey is null, it means we remove this object.
			// Don't worry about FHIR requirement. We just remove it.
			if (filter.isNull(currentFilterKey)) {
				resource.remove(currentKey);
				continue;
			}

			// Move on and evaluate the JSON node of this key.
			Object childFilter = filter.get(currentFilterKey);
			Object resourceObject = resource.get(currentKey);

			// Resourde has this key and it is not null. Check among all three types and act
			// based on the type.
			if (childFilter instanceof String) {
				// If it's String, then it's the last leaf. Null is handle above.
				// Thus, if we have this key defined AND if returned false, we should stop and
				// return false as we do
				// AND matching for all filter data elements.

				if (!(resourceObject instanceof String)) {
					// safety check. We should have the same JSON type. If not, just ignore this.
					continue;
				}
				if (!processJSONString(currentKey, resource, (String) resourceObject, (String) childFilter, replace)) {
					retv = false;
				}
			} else if (childFilter instanceof JSONObject) {
				// We can't really decide now. Move on to the child(ren).
				if (!(resourceObject instanceof JSONObject)) {
					// safety check. We should have the same JSON type. If not, just ignore this.
					continue;
				}
				if (!processJSONObject((JSONObject) resourceObject, (JSONObject) childFilter)) {
					retv = false;
				}
			} else if (childFilter instanceof JSONArray) {
				if (!(resourceObject instanceof JSONArray)) {
					// safety check. We should have the same JSON type. If not just ignore this key
					// element.
					continue;
				}
				if (!processJSONArray((JSONArray) resourceObject, (JSONArray) childFilter)) {
					retv = false;
				}
			} else {
				// non String value
				if (!processJSONValue(currentKey, resource, (Object) resourceObject, (Object) childFilter, replace)) {
					retv = false;
				}
			}
		}
		return retv;
	}

	private boolean processJSONArray(JSONArray resource, JSONArray filter) {
		boolean retv = false;
		for (int i = 0; i < filter.length(); i++) {
			Object filterJson = filter.get(i);

			// The array order between FHIR and filter do not match. So, we need to iterate
			// and compare each item
			// in the FHIR.
			boolean match = false;
			for (int j = 0; j < resource.length(); j++) {
				Object resourceJson = resource.get(j);
				if (resourceJson instanceof String && filterJson instanceof String) {
					// It would not be true as all FHIR list contains another JSON object. But, if
					// this happens, the FHIR
					// should have that as well.
					if (processJSONString(j, resource, (String) resourceJson, (String) filterJson, false)) {
						match = true;
						// We found a match. Break out.
						break;
					}
				} else if (resourceJson instanceof JSONObject && filterJson instanceof JSONObject) {
					if (processJSONObject((JSONObject) resourceJson, (JSONObject) filterJson)) {
						match = true;
						// We found a match. Break out.
						break;
					}
				} else if (resourceJson instanceof JSONArray && filterJson instanceof JSONArray) {
					if (processJSONArray((JSONArray) resourceJson, (JSONArray) filterJson)) {
						match = true;
						// We found a match. Break out.
						break;
					}
				} else {
					if (!processJSONValue(j, resource, (Object) resourceJson, (Object) filterJson, false)) {
						retv = false;
					}
				}
			}

			if (retv) {
				// We had matching one. If another filter component that is not true. We are
				// done. Everything should match.
				if (match == false) {
					retv = false;
					break;
				}
			} else {
				retv = match;
			}
		}

		return retv;
	}

	private boolean processJSONString(String key, JSONObject parentResource, String resource, String filter,
			boolean replace) {
		if (resource.equalsIgnoreCase(filter)) {
			return true;
		} else {
			if (replace)
				parentResource.put(key, filter);
			return false;
		}
	}

	private boolean processJSONString(int index, JSONArray parentResource, String resource, String filter,
			boolean replace) {
		if (resource.equalsIgnoreCase(filter)) {
			return true;
		} else {
			if (replace)
				parentResource.put(index, filter);
			return false;
		}
	}

	private boolean processJSONValue(String key, JSONObject parentResource, Object resource, Object filter,
			boolean replace) {
		if (resource == filter) {
			return true;
		} else {
			if (replace)
				parentResource.put(key, filter);
			return false;
		}
	}

	private boolean processJSONValue(int index, JSONArray parentResource, Object resource, Object filter,
			boolean replace) {
		if (resource == filter) {
			return true;
		} else {
			if (replace)
				parentResource.put(index, filter);
			return false;
		}
	}
}
