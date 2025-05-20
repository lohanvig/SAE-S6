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
import sae.semestre.six.appointment.dto.AppointmentMapper;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.appointment.request.AppointmentRequest;
import sae.semestre.six.appointment.request.AvailableSlotRequest;
import sae.semestre.six.appointment.service.AppointmentService;
import sae.semestre.six.timeslot.TimeSlot;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * @return {@link ResponseEntity} contenant un {@link Appointment} en cas de succès,
     *         ou un message d'erreur en cas d'échec
     */
    @PostMapping("/appointment")
    public ResponseEntity<?> scheduleAppointment(@RequestBody AppointmentRequest request) {
        try {
            Appointment result = appointmentService.scheduleAppointment(
                    request.getDoctorNumber(),
                    request.getPatientNumber(),
                    request.getRoomNumber(),
                    request.getAppointmentDate(),
                    request.getDuration()
            );
            return ResponseEntity.ok(AppointmentMapper.toDto(result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère les créneaux horaires disponibles pour un médecin à une date donnée.
     *
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Date}
     *         représentant les heures disponibles
     */
    @GetMapping("/available-slots")
    public ResponseEntity<List<TimeSlot>> getAvailableSlots(
            @RequestBody AvailableSlotRequest request) {
        return ResponseEntity.ok(appointmentService.getAvailableSlots(request.getDoctorId(), request.getDate()));
    }

    /**
     * Récupère tous les rendez-vous associés à un patient donné.
     *
     * @param patientNumber {@link String} identifiant unique du patient
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/patient/{patientNumber}")
    public ResponseEntity<List<Appointment>> getAppointmentsByPatientId(@PathVariable String patientNumber) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatientId(patientNumber));
    }

    /**
     * Récupère tous les rendez-vous associés à un médecin donné.
     *
     * @param doctorNumber {@link String} identifiant unique du médecin
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByDoctorId(@PathVariable String doctorNumber) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctorId(doctorNumber));
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
