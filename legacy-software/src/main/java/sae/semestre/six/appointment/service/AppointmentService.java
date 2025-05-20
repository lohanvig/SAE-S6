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
import sae.semestre.six.appointment.dto.AppointmentDto;
import sae.semestre.six.appointment.dto.AppointmentMapper;
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
    public AppointmentDto scheduleAppointment(String doctorNumber, String patientNumber, String roomNumber, Date appointmentDate, int durationMinutes) {
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

        return AppointmentMapper.toDto(appointment);
    }

    /**
     * Retourne les créneaux horaires disponibles pour un médecin à une date donnée.
     *
     * @param doctorNumber numero du médecin
     * @param date     Date cible pour les créneaux disponibles
     * @return Liste {@link List<Date>} des créneaux horaires disponibles
     */
    public List<TimeSlot> getAvailableSlots(String doctorNumber, Date date) {
        Doctor doctor = null;
        try {
            doctor = doctorDao.findByDoctorNumber(doctorNumber);
        } catch (Exception e) {
            new IllegalArgumentException("Médecin non trouvé");
        }

        Calendar dayStart = Calendar.getInstance();
        dayStart.setTime(date);
        dayStart.set(Calendar.HOUR_OF_DAY, doctor.getWorkStartHour());
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);

        Calendar dayEnd = (Calendar) dayStart.clone();
        dayEnd.set(Calendar.HOUR_OF_DAY, doctor.getWorkEndHour());

        List<Appointment> appointments = appointmentDao.findByDoctorIdByDate(doctor.getId(), date);

        List<TimeSlot> availableSlots = new ArrayList<>();
        Calendar slotStart = (Calendar) dayStart.clone();

        for (Appointment appointment : appointments) {
            Date appStart = appointment.getDate();
            Calendar appStartCal = Calendar.getInstance();
            appStartCal.setTime(appStart);

            Calendar appEndCal = (Calendar) appStartCal.clone();
            appEndCal.add(Calendar.MINUTE, appointment.getDuration());

            // Si trou entre slotStart et appStartCal
            if (slotStart.before(appStartCal)) {
                availableSlots.add(new TimeSlot(slotStart.getTime(), appStartCal.getTime()));
            }

            // Mettre à jour slotStart pour le prochain tour
            if (slotStart.before(appEndCal)) {
                slotStart = (Calendar) appEndCal.clone();
            }
        }

        // Dernier créneau entre fin dernier rendez-vous et fin journée
        if (slotStart.before(dayEnd)) {
            availableSlots.add(new TimeSlot(slotStart.getTime(), dayEnd.getTime()));
        }

        return availableSlots;
    }

    /**
     * Valide la faisabilité d'un créneau de rendez-vous pour un médecin et une salle,
     * en vérifiant qu'il ne dépasse pas les horaires de travail du médecin, qu'il ne
     * chevauche pas d'autres rendez-vous du médecin, ni d'autres rendez-vous déjà
     * planifiés dans la salle.
     *
     * @param doctor          Le médecin concerné
     * @param room            La salle concernée
     * @param appointmentDate La date et heure de début du rendez-vous
     * @param durationMinutes La durée du rendez-vous en minutes
     * @throws UnvailableException Si le rendez-vous n'est pas planifiable
     */
    private void validateAppointmentSlot(Doctor doctor, Room room, Date appointmentDate, int durationMinutes) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(appointmentDate);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.MINUTE, durationMinutes);

        int startHour = startCal.get(Calendar.HOUR_OF_DAY);
        int endHour = endCal.get(Calendar.HOUR_OF_DAY);

        System.out.println(appointmentDate);
        System.out.println(startHour);
        System.out.println(endHour);

        System.out.println(doctor.getWorkStartHour());
        System.out.println(doctor.getWorkEndHour());

        // Vérification horaires de travail
        if (startHour < doctor.getWorkStartHour() || endHour >= doctor.getWorkEndHour()) {
            throw new UnvailableException("Le rendez-vous dépasse les horaires de travail du médecin.");
        }

        Date startTime = startCal.getTime();
        Date endTime = endCal.getTime();

        // Vérif conflits médecin
        List<Appointment> doctorAppointments = appointmentDao.findByDoctorId(doctor.getId());
        for (Appointment existing : doctorAppointments) {
            if (hasConflict(startTime, endTime, existing.getDate(), durationMinutes)) {
                throw new UnvailableException("Le médecin a déjà un rendez-vous qui chevauche ce créneau.");
            }
        }

        // Vérif conflits salle
        List<Appointment> roomAppointments = appointmentDao.findByRoomIdByDate(room.getId(), appointmentDate);
        for (Appointment existing : roomAppointments) {
            if (hasConflict(startTime, endTime, existing.getDate(), durationMinutes)) {
                throw new UnvailableException("La salle est déjà utilisée pendant ce créneau.");
            }
        }
    }

    /**
     * Vérifie s'il existe un conflit entre deux intervalles de rendez-vous.
     *
     * @param newStart               Date de début du nouveau rendez-vous
     * @param newEnd                 Date de fin du nouveau rendez-vous
     * @param existingStart          Date de début du rendez-vous existant
     * @param existingDurationMinutes Durée en minutes du rendez-vous existant
     * @return true si les deux rendez-vous se chevauchent, false sinon
     */
    private boolean hasConflict(Date newStart, Date newEnd, Date existingStart, int existingDurationMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(existingStart);
        cal.add(Calendar.MINUTE, existingDurationMinutes);
        Date existingEnd = cal.getTime();

        return newStart.before(existingEnd) && newEnd.after(existingStart);
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
        Doctor doctor = null;
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
