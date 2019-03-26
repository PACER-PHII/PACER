package gatech.edu.STIECR.DB.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.STIECR.DB.model.ECRJob;

@Repository
public interface ECRJobRepository extends JpaRepository<ECRJob, Integer>{
	List<ECRJob> findByReportIdOrderByIdDesc(Integer reportId);
}