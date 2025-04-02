package sae.semestre.six.prescription.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.prescription.model.Prescription;
import java.util.List;

public interface PrescriptionDao extends GenericDao<Prescription, Long> {
    List<Prescription> findByPatientId(Long patientId);
} 