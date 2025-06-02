package sae.semestre.six.treatment.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sae.semestre.six.treatment.model.Treatment;

@Repository
public interface TreatmentDao extends JpaRepository<Treatment, Long> {

}
