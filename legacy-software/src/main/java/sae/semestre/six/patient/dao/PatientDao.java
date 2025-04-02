package sae.semestre.six.patient.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.patient.model.Patient;
import java.util.List;

public interface PatientDao extends GenericDao<Patient, Long> {
    Patient findByPatientNumber(String patientNumber);
    List<Patient> findByLastName(String lastName);
} 