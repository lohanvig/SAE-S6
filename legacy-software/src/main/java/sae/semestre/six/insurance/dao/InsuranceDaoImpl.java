package sae.semestre.six.insurance.dao;

import org.springframework.stereotype.Repository;
import sae.semestre.six.base.dao.AbstractHibernateDao;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.insurance.model.Insurance;

@Repository
public class InsuranceDaoImpl extends AbstractHibernateDao<Insurance, Long> implements InsuranceDao {
    @Override
    public Insurance getInsuranceByPatient(Long patientId) {
        return (Insurance) getEntityManager()
                .createQuery("FROM Insurance WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getSingleResult();
    }
}
