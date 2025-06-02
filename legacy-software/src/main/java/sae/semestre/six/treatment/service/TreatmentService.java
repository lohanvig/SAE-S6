package sae.semestre.six.treatment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.patient.dao.PatientDao;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.treatment.dao.TreatmentDao;
import sae.semestre.six.treatment.model.Treatment;

import java.util.NoSuchElementException;


@Service
public class TreatmentService {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private TreatmentDao treatmentDao;

    /**
     * Ajoute un traitement pour un patient existant
     *
     * Cette méthode commence par vérifier que le patient associé au traitement
     * existe dans la base de données, en recherchant son numéro de patient.
     * Si le patient n'est pas trouvé, une exception est levée.
     * Si le patient est bien présent, le traitement est enregistré via le DAO correspondant.
     *
     *
     * @param treatment le traitement à ajouter, lié à l'historique d'un patient.
     * @throws NoSuchElementException si aucun patient correspondant au numéro fourni n’est trouvé.
     */
    public void addTreatment(Treatment treatment) {
        // On vérifie que l'ID du patient existe
        Patient patient;
        try {
            patient = patientDao.findByPatientNumber(treatment.getPatientHistory().getPatient().getPatientNumber());
        } catch (Exception e) {
            throw new NoSuchElementException("Patient non trouvé");
        }

        treatmentDao.save(treatment);
    }
}
