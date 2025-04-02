package sae.semestre.six.prescription.dao;

import sae.semestre.six.base.dao.AbstractHibernateDao;
import sae.semestre.six.prescription.model.Prescription;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PrescriptionDaoImpl extends AbstractHibernateDao<Prescription, Long> implements PrescriptionDao {
    
    @Override
    @SuppressWarnings("unchecked")
    public List<Prescription> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Prescription WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }
} 