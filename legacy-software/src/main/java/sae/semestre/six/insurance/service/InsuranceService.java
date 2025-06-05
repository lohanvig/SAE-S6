package sae.semestre.six.insurance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.insurance.dao.InsuranceDao;
import sae.semestre.six.insurance.model.Insurance;

@Service
public class InsuranceService {

    @Autowired
    private InsuranceDao insuranceDao;

    public Insurance getByPatientId(Long id) {
        return insuranceDao.getInsuranceByPatient(id);
    }
}
