/**
 * AppointmentService.java
 *
 * Service métier responsable de la gestion des rendez-vous médicaux,
 * incluant la planification et la consultation des créneaux disponibles.
 * Ce service permet de planifier des rendez-vous, vérifier la disponibilité
 * des créneaux horaires et récupérer les rendez-vous par différentes
 * critères tels que la date, le médecin ou le patient.
 */

package sae.semestre.six.appointment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.dao.AppointmentDao;
import sae.semestre.six.appointment.exception.UnvailableException;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.dao.PatientDaoImpl;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.service.EmailService;

import java.util.*;

/**
 * Service qui gère la planification des rendez-vous médicaux,
 * vérifie la disponibilité des créneaux horaires, récupère les rendez-vous
 * par critères, et envoie des notifications par email.
 */
@Service
public class AppointmentService {

    private final AppointmentDao appointmentDao;
    private final DoctorDao doctorDao;
    private final EmailService emailService = EmailService.getInstance();
    private final PatientDaoImpl patientDao;

    /**
     * Constructeur du service avec injection des dépendances.
     *
     * @param appointmentDao DAO des rendez-vous
     * @param doctorDao      DAO des médecins
     * @param patientDao     DAO des patients
     */
    @Autowired
    public AppointmentService(AppointmentDao appointmentDao, DoctorDao doctorDao, PatientDaoImpl patientDao) {
        this.appointmentDao = appointmentDao;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
    }

    /**
     * Planifie un rendez-vous pour un médecin et un patient à une date donnée.
     *
     * @param doctorNumber        numéro du médecin
     * @param patientNumber       numéro du patient
     * @param appointmentDate Date du rendez-vous
     * @return {@link Appointment} objet représentant le rendez-vous créé
     * @throws UnvailableException Si le créneau est déjà réservé ou si l'heure est hors des plages permises
     */
    public Appointment scheduleAppointment(String doctorNumber, String patientNumber, Date appointmentDate) {
        Doctor doctor = doctorDao.findByDoctorNumber(doctorNumber);
        if (doctor == null) {
            throw new IllegalArgumentException("Médecin non trouvé");
        }

        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctor.getId());
        for (Appointment existing : doctorAppointments) {
            if (existing.getAppointmentDate().equals(appointmentDate)) {
                throw new UnvailableException("Le médecin n'est pas disponible à ce créneau.");
            }
        }

        Patient patient = patientDao.findByPatientNumber(patientNumber);
        if (patient == null) {
            throw new IllegalArgumentException("Patient non trouvé");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(appointmentDate);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        // Vérification de l'heure de disponibilité du médecin
        if (hour < doctor.getWorkStartHour() || hour >= doctor.getWorkEndHour()) {
            throw new UnvailableException("Les créneaux sont uniquement réservables entre "
                    + doctor.getWorkStartHour() + "h00 et " + doctor.getWorkEndHour() + "h00.");
        }

        // Création et sauvegarde du rendez-vous
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStatus("SCHEDULED");

        // Génération d'un numéro unique pour le rendez-vous
        appointment.setAppointmentNumber(UUID.randomUUID().toString());

        appointmentDao.save(appointment);

        emailService.sendEmail(
                doctor.getEmail(),
                "Nouveau rendez-vous planifié",
                "Vous avez un nouveau rendez-vous prévu pour le " + appointmentDate
        );

        return appointment;
    }

    /**
     * Retourne les créneaux horaires disponibles pour un médecin à une date donnée.
     *
     * @param doctorNumber numero du médecin
     * @param date     Date cible pour les créneaux disponibles
     * @return Liste {@link List<Date>} des créneaux horaires disponibles
     */
    public List<Date> getAvailableSlots(String doctorNumber, Date date) {
        // Récupération du médecin à partir de son ID
        Doctor doctor = doctorDao.findByDoctorNumber(doctorNumber);
        if (doctor == null) {
            throw new IllegalArgumentException("Médecin non trouvé");
        }

        List<Appointment> appointments = appointmentDao.findByDoctorId(doctor.getId());
        List<Date> availableSlots = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        // Vérification des créneaux horaires disponibles entre les horaires de travail du médecin
        for (int hour = doctor.getWorkStartHour(); hour < doctor.getWorkEndHour(); hour++) {
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, 0);
            int finalHour = hour;
            boolean slotTaken = appointments.stream()
                    .anyMatch(app -> {
                        Calendar appCal = Calendar.getInstance();
                        appCal.setTime(app.getAppointmentDate());
                        return appCal.get(Calendar.HOUR_OF_DAY) == finalHour &&
                                appCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
                                appCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR);
                    });

            if (!slotTaken) {
                availableSlots.add(cal.getTime());
            }
        }

        return availableSlots;
    }

    /**
     * Retourne la liste des rendez-vous dans une plage de dates donnée.
     *
     * @param startDate Date de début de la plage
     * @param endDate   Date de fin de la plage
     * @return Liste {@link List<Appointment>} des rendez-vous dans la plage de dates
     */
    public List<Appointment> getAppointmentsByDateRange(Date startDate, Date endDate) {
        return appointmentDao.findByDateRange(startDate, endDate);
    }

    /**
     * Retourne la liste des rendez-vous d'un médecin donné.
     *
     * @param doctorNumber numéro du médecin
     * @return Liste {@link List<Appointment>} des rendez-vous du médecin
     */
    public List<Appointment> getAppointmentsByDoctorId(String doctorNumber) {
        // Récupération du médecin à partir de son numéro
        Doctor doctor = doctorDao.findByDoctorNumber(doctorNumber);
        if (doctor == null) {
            throw new IllegalArgumentException("Médecin non trouvé");
        }
        return appointmentDao.findByDoctorId(doctor.getId());
    }

    /**
     * Retourne la liste des rendez-vous d'un patient donné.
     *
     * @param patientNumber numéro du patient
     * @return Liste {@link List<Appointment>} des rendez-vous du patient
     */
    public List<Appointment> getAppointmentsByPatientId(String patientNumber) {
        // Récupération du patient à partir de son numéro
        Patient patient = patientDao.findByPatientNumber(patientNumber);
        if (patient == null) {
            throw new IllegalArgumentException("Patient non trouvé");
        }

        return appointmentDao.findByPatientId(patient.getId());
    }
}
