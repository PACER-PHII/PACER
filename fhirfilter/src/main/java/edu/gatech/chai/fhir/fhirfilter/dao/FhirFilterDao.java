package edu.gatech.chai.fhir.fhirfilter.dao;

import java.sql.Connection;
import java.util.List;

import edu.gatech.chai.fhir.fhirfilter.model.FilterData;

public interface FhirFilterDao {
	public Connection connect();
	
	public int save(FilterData filterData);
	public void update(FilterData filterData);
	public void delete(Integer id);
	public List<FilterData> get();
	public FilterData getById(Integer id);
	public FilterData getByName(String name);
}
