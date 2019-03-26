package edu.gatech.chai.fhir.fhirfilter.api;

import edu.gatech.chai.fhir.fhirfilter.model.FilterData;
import edu.gatech.chai.fhir.fhirfilter.model.ListFilterData;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ManageApiControllerIntegrationTest {

    @Autowired
    private ManageApi api;

    @Test
    public void manageGetTest() throws Exception {
        ResponseEntity<ListFilterData> responseEntity = api.manageGet();
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void manageIdDeleteTest() throws Exception {
        Integer id = 56;
        ResponseEntity<Void> responseEntity = api.manageIdDelete(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void manageIdGetTest() throws Exception {
        Integer id = 56;
        ResponseEntity<FilterData> responseEntity = api.manageIdGet(id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void manageIdPutTest() throws Exception {
        FilterData body = new FilterData();
        Integer id = 56;
        ResponseEntity<Void> responseEntity = api.manageIdPut(body, id);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void managePostTest() throws Exception {
        FilterData body = new FilterData();
        ResponseEntity<Void> responseEntity = api.managePost(body);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void manageProfileNameGetTest() throws Exception {
        String name = "name_example";
        ResponseEntity<FilterData> responseEntity = api.manageProfileNameGet(name);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
