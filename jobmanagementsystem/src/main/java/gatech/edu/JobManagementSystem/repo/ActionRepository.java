package gatech.edu.JobManagementSystem.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.JobManagementSystem.model.Action;

@Repository
public interface ActionRepository extends JpaRepository<Action, Integer>{

}
