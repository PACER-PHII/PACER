package gatech.edu.JobManagementSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.JobManagementSystem.model.JsonTest;

@Repository
public interface JsonTestRepository extends JpaRepository<JsonTest, Integer>{

}
