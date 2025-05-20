package sae.semestre.six.patient.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.patient.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientDao extends GenericDao<Patient, Long> {
    Optional<Patient> findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 