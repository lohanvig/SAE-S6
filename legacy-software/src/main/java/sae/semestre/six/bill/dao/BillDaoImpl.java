package sae.semestre.six.bill.dao;

import sae.semestre.six.bill.model.Bill;
import sae.semestre.six.base.dao.AbstractHibernateDao;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

/**
 * Implémentation DAO de la gestion des entités {@link Bill} avec Hibernate.
 * Fournit des méthodes spécifiques de recherche de factures selon différents critères.
 */
@Repository
public class BillDaoImpl extends AbstractHibernateDao<Bill, Long> implements BillDao {

    /**
     * Recherche une facture à partir de son numéro unique.
     *
     * @param billNumber le numéro de la facture
     * @return la facture correspondante
     */
    @Override
    public Bill findByBillNumber(String billNumber) {
        return (Bill) getEntityManager()
                .createQuery("FROM Bill WHERE billNumber = :billNumber")
                .setParameter("billNumber", billNumber)
                .getSingleResult();
    }

    /**
     * Recherche toutes les factures associées à un patient donné.
     *
     * @param patientId l'identifiant du patient
     * @return une liste de factures correspondant au patient
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByPatientId(Long patientId) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE patient.id = :patientId")
                .setParameter("patientId", patientId)
                .getResultList();
    }

    /**
     * Recherche toutes les factures associées à un médecin donné.
     *
     * @param doctorId l'identifiant du médecin
     * @return une liste de factures correspondant au médecin
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByDoctorId(Long doctorId) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE doctor.id = :doctorId")
                .setParameter("doctorId", doctorId)
                .getResultList();
    }

    /**
     * Recherche les factures émises dans une période de temps donnée.
     *
     * @param startDate date de début de la période
     * @param endDate date de fin de la période
     * @return une liste de factures comprises dans l'intervalle de dates
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByDateRange(Date startDate, Date endDate) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE billDate BETWEEN :startDate AND :endDate")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    /**
     * Recherche les factures ayant un statut donné.
     *
     * @param status le statut recherché (ex. : "PENDING", "PAID")
     * @return une liste de factures avec ce statut
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Bill> findByStatus(String status) {
        return getEntityManager()
                .createQuery("FROM Bill WHERE status = :status")
                .setParameter("status", status)
                .getResultList();
    }

    /**
     * Calcule le chiffre d'affaires total à partir des montants des factures valides (celles dont le hash est défini).
     *
     * @return le chiffre d'affaires total sous forme de double
     */
    public Double getTotalRevenue() {
        return getEntityManager()
                .createQuery(
                        "SELECT SUM(b.totalAmount) FROM Bill b WHERE b.hash IS NOT NULL",
                        Double.class
                )
                .getSingleResult();
    }
}