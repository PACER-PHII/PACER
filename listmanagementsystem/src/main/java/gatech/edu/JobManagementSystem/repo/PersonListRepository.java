package gatech.edu.JobManagementSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.JobManagementSystem.model.PersonList;

@Repository
public interface PersonListRepository extends JpaRepository<PersonList, String>{
	public PersonList findByName(String name);
}