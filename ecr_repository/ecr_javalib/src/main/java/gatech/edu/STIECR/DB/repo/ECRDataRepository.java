package gatech.edu.STIECR.DB.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import gatech.edu.STIECR.DB.model.ECRData;
import gatech.edu.STIECR.DB.model.ECRJob;

@Repository
public interface ECRDataRepository extends JpaRepository<ECRData, Integer> {
	List<ECRData> findByEcrIdOrderByVersionDesc(Integer ecrId);
	List<ECRData> findByLastNameOrderByVersionDesc(String lastName,Pageable pageable);
	List<ECRData> findByFirstNameOrderByVersionDesc(String firstName,Pageable pageable);
	List<ECRData> findByZipCodeOrderByVersionDesc(String zipCode,Pageable pageable);
	List<ECRData> findByDiagnosisCodeOrderByVersionDesc(String diagnosisCode,Pageable pageable);
	List<ECRData> findByLastNameAndFirstNameOrderByVersionDesc(String lastName,String firstName,Pageable pageable);
	List<ECRData> findByLastNameAndZipCodeOrderByVersionDesc(String lastName,String zipCode,Pageable pageable);
	List<ECRData> findByLastNameAndDiagnosisCodeOrderByVersionDesc(String lastName,String diagnosisCode,Pageable pageable);
	List<ECRData> findByFirstNameAndZipCodeOrderByVersionDesc(String firstName,String zipCode,Pageable pageable);
	List<ECRData> findByFirstNameAndDiagnosisCodeOrderByVersionDesc(String firstName,String diagnosisCode,Pageable pageable);
	List<ECRData> findByZipCodeAndDiagnosisCodeOrderByVersionDesc(String zipCode,String diagnosisCode,Pageable pageable);
	List<ECRData> findByLastNameAndFirstNameAndZipCodeOrderByVersionDesc(String lastName,String firstName,String zipCode,Pageable pageable);
	List<ECRData> findByLastNameAndFirstNameAndDiagnosisCodeOrderByVersionDesc(String lastName,String firstName,String diagnosisCode,Pageable pageable);
	List<ECRData> findByLastNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(String lastName,String zipCode,String diagnosisCode,Pageable pageable);
	List<ECRData> findByFirstNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(String firstName,String zipCode,String diagnosisCode,Pageable pageable);
	List<ECRData> findByLastNameAndFirstNameAndZipCodeAndDiagnosisCodeOrderByVersionDesc(String lastName,String firstName,String zipCode,String diagnosisCode,Pageable pageable);
}