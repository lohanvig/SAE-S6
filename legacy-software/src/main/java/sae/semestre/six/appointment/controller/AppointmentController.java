/**
 * AppointmentController.java
 *
 * Contrôleur REST pour la gestion des rendez-vous médicaux.
 * Il permet de planifier un rendez-vous, consulter les créneaux disponibles
 * et interroger les rendez-vous par patient, médecin ou plage de dates.
 */

package sae.semestre.six.appointment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.appointment.service.AppointmentService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/scheduling")
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Constructeur du contrôleur avec injection du service de rendez-vous.
     *
     * @param appointmentService Service métier de gestion des rendez-vous
     */
    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    /**
     * Planifie un rendez-vous médical entre un patient et un médecin à une date précise.
     *
     * @param doctorId        {@link Long} identifiant unique du médecin
     * @param patientId       {@link Long} identifiant unique du patient
     * @param appointmentDate {@link Date} date et heure du rendez-vous souhaité
     * @return {@link ResponseEntity} contenant un {@link Appointment} en cas de succès,
     *         ou un message d'erreur en cas d'échec
     */
    @PostMapping("/appointment")
    public ResponseEntity<?> scheduleAppointment(
            @RequestParam Long doctorId,
            @RequestParam Long patientId,
            @RequestParam Date appointmentDate) {
        try {
            Appointment result = appointmentService.scheduleAppointment(doctorId, patientId, appointmentDate);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Récupère les créneaux horaires disponibles pour un médecin à une date donnée.
     *
     * @param doctorId {@link Long} identifiant unique du médecin
     * @param date     {@link Date} date cible pour laquelle vérifier les créneaux disponibles
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Date}
     *         représentant les heures disponibles
     */
    @GetMapping("/available-slots")
    public ResponseEntity<List<Date>> getAvailableSlots(
            @RequestParam Long doctorId,
            @RequestParam Date date) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(doctorId, date));
    }

    /**
     * Récupère tous les rendez-vous associés à un patient donné.
     *
     * @param patientId {@link Long} identifiant unique du patient
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable Long patientId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientId));
    }

    /**
     * Récupère tous les rendez-vous associés à un médecin donné.
     *
     * @param doctorId {@link Long} identifiant unique du médecin
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorId));
    }

    /**
     * Récupère les rendez-vous planifiés dans une plage de dates donnée.
     *
     * @param startDate {@link Date} date de début de la plage
     * @param endDate   {@link Date} date de fin de la plage
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/range")
    public ResponseEntity<List<Appointment>> getAppointmentsByDateRange(
            @RequestParam Date startDate,
            @RequestParam Date endDate) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(startDate, endDate));
    }
}
