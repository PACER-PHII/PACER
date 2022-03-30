package gatech.edu.JobManagementSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.JobManagementSystem.model.Person;

@Repository
public interface PersonRepository extends JpaRepository<Person, String>{
	public Person findById(Integer id);
}