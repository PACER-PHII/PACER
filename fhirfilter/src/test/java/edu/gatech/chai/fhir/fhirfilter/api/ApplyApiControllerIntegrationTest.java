package edu.gatech.chai.fhir.fhirfilter.api;


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
public class ApplyApiControllerIntegrationTest {

    @Autowired
    private ApplyApi api;

    @Test
    public void applyIdsPostTest() throws Exception {
        String body = "body_example";
        String ids = "ids_example";
        ResponseEntity<String> responseEntity = api.applyIdsPost(body, ids);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

    @Test
    public void applyPostTest() throws Exception {
        String body = "body_example";
        ResponseEntity<String> responseEntity = api.applyPost(body);
        assertEquals(HttpStatus.NOT_IMPLEMENTED, responseEntity.getStatusCode());
    }

}
