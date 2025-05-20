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
import org.springframework.transaction.annotation.Transactional;
import sae.semestre.six.appointment.dao.AppointmentDao;
import sae.semestre.six.appointment.exception.UnvailableException;
import sae.semestre.six.doctor.dao.DoctorDao;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.dao.PatientDaoImpl;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.room.dao.RoomDaoImpl;
import sae.semestre.six.room.model.Room;
import sae.semestre.six.service.EmailService;
import sae.semestre.six.timeslot.TimeSlot;
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
    private final RoomDaoImpl roomDao;

    /**
     * Constructeur du service avec injection des dépendances.
     *
     * @param appointmentDao DAO des rendez-vous
     * @param doctorDao      DAO des médecins
     * @param patientDao     DAO des patients
     * @param roomDao        DAO des salles
     */
    @Autowired
    public AppointmentService(AppointmentDao appointmentDao, DoctorDao doctorDao, PatientDaoImpl patientDao, RoomDaoImpl roomDao) {
        this.appointmentDao = appointmentDao;
        this.doctorDao = doctorDao;
        this.patientDao = patientDao;
        this.roomDao = roomDao;
    }

    /**
     * Planifie un rendez-vous pour un médecin et un patient à une date donnée.
     *
     * @param doctorNumber         numéro du médecin
     * @param patientNumber        numéro du patient
     * @param appointmentDate      Date du rendez-vous
     * @return {@link Appointment} objet représentant le rendez-vous créé
     * @throws UnvailableException Si le créneau est déjà réservé ou si l'heure est hors des plages permises
     */
    @Transactional
    public Appointment scheduleAppointment(String doctorNumber, String patientNumber, String roomNumber, Date appointmentDate, int durationMinutes) {
        Doctor doctor;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
            throw new IllegalArgumentException("Médecin non trouvé");
        }

        Patient patient;
        try {
            patient = patientDao.findByPatientNumber(patientNumber);
        } catch (Exception e) {
            throw new IllegalArgumentException("Patient non trouvé");
        }

        Room room;
        try {
            room = roomDao.findByRoomNumber(roomNumber);
        } catch (Exception e) {
            throw new IllegalArgumentException("Salle non trouvée");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setRoom(room);
        appointment.setDate(appointmentDate);
        appointment.setStatus("SCHEDULED");
        appointment.setAppointmentNumber(UUID.randomUUID().toString());

        // Vérifie la validité du créneau
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctor.getId());
        List<Appointment> roomAppointments = appointmentDao.findByRoomIdByDate(room.getId(), appointmentDate);
        appointment.validateSlot(doctorAppointments, roomAppointments);

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
     * @return la liste des créneaux horaires disponibles
     */
    public List<TimeSlot> getAvailableSlots(String doctorNumber, Date date) {
        Doctor doctor;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
            throw new IllegalArgumentException("Médecin non trouvé");
        }
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctor.getId());
        return TimeSlot.getDoctorAvailableTimeSlotByDay(date, doctor, doctorAppointments);
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
        Doctor doctor;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
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
