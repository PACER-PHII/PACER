package gatech.edu.JobManagementSystem.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import gatech.edu.JobManagementSystem.model.ListRunType;
import gatech.edu.JobManagementSystem.model.Person;
import gatech.edu.JobManagementSystem.model.PersonList;

@RestController
@RequestMapping(value = "/asyncTest")
public class AsyncTestController {
	private AtomicInteger counter;
	private static final Logger log = LoggerFactory.getLogger(AsyncTestController.class);
	
	public AsyncTestController() {
		counter = new AtomicInteger();
	}
	
	@RequestMapping(value = "/re", method = RequestMethod.GET)
	public ResponseEntity<?> someResponseEntityTest(){
		log.info("Response Entity Request");
		return ResponseEntity.ok().body(this.fakeList());
	}
	
	@RequestMapping(value = "/callable", method = RequestMethod.GET)
	public Callable<ResponseEntity<?>> someCallableTest(){
		log.info("Callable Response Entity Request");
		return () -> ResponseEntity.ok(this.fakeList());
	}
	
	@RequestMapping(value = "/deferred", method = RequestMethod.GET)
	public DeferredResult<ResponseEntity<?>> timeDeferred() {
	    log.info("Deferred time request");
	    DeferredResult<ResponseEntity<?>> result = new DeferredResult<>();

	    new Thread(() -> {
	        result.setResult(ResponseEntity.ok(fakeList()));
	    }, "MyThread-" + counter.incrementAndGet()).start();

	    return result;
	}
	
	private static PersonList fakeList() {
		log.info("Creating fakeList model to return.");
		PersonList pl = new PersonList();
		pl.setJobType("testAsync");
		pl.setName("MyTestingJobList");
		Person testPerson = new Person();
		testPerson.setId(1);
		testPerson.setLabOrderDate(new Date());
		testPerson.setName("Testy Tester");
		Set<Person> personSet = new HashSet<Person>();
		personSet.add(testPerson);		
		pl.setListElements(personSet);
		pl.setRunType(ListRunType.ALL);
		return pl;
	}
}
