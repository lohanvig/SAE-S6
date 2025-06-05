package sae.semestre.six.insurance.dao;

import sae.semestre.six.base.dao.GenericDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.insurance.model.Insurance;

import java.util.List;

public interface InsuranceDao extends GenericDao<Insurance, Long> {
    Insurance getInsuranceByPatient(Long patientId);
}