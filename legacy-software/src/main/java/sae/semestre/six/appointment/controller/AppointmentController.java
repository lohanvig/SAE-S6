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
import sae.semestre.six.appointment.dto.AppointmentDto;
import sae.semestre.six.appointment.dto.AppointmentMapper;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.appointment.request.AppointmentRequest;
import sae.semestre.six.appointment.request.AvailableSlotRequest;
import sae.semestre.six.appointment.service.AppointmentService;
import sae.semestre.six.timeslot.TimeSlot;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
            Appointment result = appointmentService.scheduleAppointment(request);
            return ResponseEntity.ok(AppointmentMapper.toDto(result));
        } catch (NoSuchElementException e) {
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
    @GetMapping("/doctor/available-slots")
    public ResponseEntity<?> getAvailableSlotsForDoctor(
            @RequestBody AvailableSlotRequest request) {
        try {
            return ResponseEntity.ok(appointmentService.getAvailableSlots(request.getDoctorId(), request.getDate()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère tous les rendez-vous associés à un patient donné.
     *
     * @param patientNumber {@link String} identifiant unique du patient
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/patient/{patientNumber}")
    public ResponseEntity<?> getAppointmentsByPatientId(@PathVariable String patientNumber) {
        try {
            return ResponseEntity.ok(AppointmentMapper.ListToDto(appointmentService.getAppointmentsByPatientId(patientNumber)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère tous les rendez-vous associés à un médecin donné.
     *
     * @param doctorNumber {@link String} identifiant unique du médecin
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/doctor/{doctorNumber}")
    public ResponseEntity<?> getAppointmentsByDoctorId(@PathVariable String doctorNumber) {
        try {
            return ResponseEntity.ok(AppointmentMapper.ListToDto(appointmentService.getAppointmentsByDoctorId(doctorNumber)));
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère les rendez-vous planifiés dans une plage de dates donnée.
     *
     * @param startDate {@link LocalDateTime} date de début de la plage
     * @param endDate   {@link LocalDateTime} date de fin de la plage
     * @return {@link ResponseEntity} contenant une {@link List} de {@link Appointment}
     */
    @GetMapping("/appointments/range")
    public ResponseEntity<?> getAppointmentsByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        try {
            return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(startDate, endDate));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }
}
