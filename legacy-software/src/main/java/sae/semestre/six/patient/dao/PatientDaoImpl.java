package sae.semestre.six.patient.dao;

import sae.semestre.six.base.dao.AbstractHibernateDao;
import sae.semestre.six.patient.model.Patient;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class PatientDaoImpl extends AbstractHibernateDao<Patient, Long> implements PatientDao {
    
    @Override
    public Optional<Patient> findByPatientNumber(String patientNumber) {
        return Optional.ofNullable((Patient) getEntityManager()
                .createQuery("FROM Patient WHERE patientNumber = :patientNumber")
                .setParameter("patientNumber", patientNumber)
                .getSingleResult());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Patient> findByLastName(String lastName) {
        return getEntityManager()
                .createQuery("FROM Patient WHERE lastName LIKE :lastName")
                .setParameter("lastName", lastName + "%")
                .getResultList();
    }
} 