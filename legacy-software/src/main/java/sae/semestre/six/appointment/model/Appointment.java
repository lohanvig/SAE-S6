package sae.semestre.six.appointment.model;

import jakarta.persistence.*;
import sae.semestre.six.appointment.exception.UnvailableException;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.patient.model.PatientHistory;
import sae.semestre.six.room.model.Room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_number", unique = true, nullable = false)
    private String appointmentNumber;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    private Room room;

    @ManyToOne
    private PatientHistory patientHistory;

    @Column(name = "appointment_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime date;

    @Column(name = "status")
    private String status;

    @Column(name = "description")
    private String description;

    @Column(name="appointmentDuration")
    private int duration;

    
    public Appointment() {
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAppointmentNumber() {
        return appointmentNumber;
    }

    public void setAppointmentNumber(String appointmentNumber) {
        this.appointmentNumber = appointmentNumber;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime appointmentDate) {
        this.date = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getRoom() { return room; }

    public void setRoom(Room room) { this.room = room; }

    public int getDuration() { return duration; }

    public void setDuration(int appointmentDuration) { this.duration = appointmentDuration; }

    /**
     * Valide la faisabilité d'un créneau de rendez-vous pour un médecin et une salle,
     * en vérifiant qu'il ne dépasse pas les horaires de travail du médecin, qu'il ne
     * chevauche pas d'autres rendez-vous du médecin, ni d'autres rendez-vous déjà
     * planifiés dans la salle.
     *
     * @param doctorAppointments rendez-vous du docteur
     * @param roomAppointments rendez-vous de la salle
     *
     * @throws UnvailableException Si le rendez-vous n'est pas planifiable
     */
    public void validateSlot(List<Appointment> doctorAppointments, List<Appointment> roomAppointments) {
        if (this.doctor == null || this.room == null || this.date == null) {
            throw new IllegalArgumentException("Impossible de vérifier la validité du créneau.");
        }

        LocalDateTime startDateTime = this.date;
        LocalDateTime endDateTime = startDateTime.plusMinutes(this.duration);

        LocalTime appointmentStartTime = startDateTime.toLocalTime();
        LocalTime appointmentEndTime = endDateTime.toLocalTime();

        LocalTime doctorStart = doctor.getWorkStartHour();
        LocalTime doctorEnd = doctor.getWorkEndHour();

        // Vérifie si le créneau respecte les horaires de travail
        if (appointmentStartTime.isBefore(doctorStart) || appointmentEndTime.isAfter(doctorEnd)) {
            throw new UnvailableException("Le rendez-vous dépasse les horaires de travail du médecin.");
        }

        // Vérifie les conflits avec les autres rendez-vous du médecin
        for (Appointment existing : doctorAppointments) {
            if (hasConflict(startDateTime, endDateTime, existing.getDate(), existing.getDuration())) {
                throw new UnvailableException("Le médecin a déjà un rendez-vous qui chevauche ce créneau.");
            }
        }

        // Vérifie les conflits avec les autres rendez-vous de la salle
        for (Appointment existing : roomAppointments) {
            if (hasConflict(startDateTime, endDateTime, existing.getDate(), existing.getDuration())) {
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
    private boolean hasConflict(LocalDateTime newStart, LocalDateTime newEnd, LocalDateTime existingStart, int existingDurationMinutes) {
        LocalDateTime existingEnd = existingStart.plusMinutes(existingDurationMinutes);
        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }
}