package edu.gatech.CQLStorage.repo;

import java.util.List;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import edu.gatech.CQLStorage.entity.CQL;

@RepositoryRestResource(collectionResourceRel = "CQLs", path = "CQL")
public interface CQLRepository extends PagingAndSortingRepository<CQL, Long> {
	List<CQL> findByName(@Param("name") String name);
}