package sae.semestre.six.appointment.model;

import jakarta.persistence.*;
import sae.semestre.six.doctor.model.Doctor;
import sae.semestre.six.patient.model.Patient;
import sae.semestre.six.patient.model.PatientHistory;
import sae.semestre.six.room.model.Room;

import java.time.LocalDateTime;
import java.util.Date;

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
    private Date date;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date appointmentDate) {
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
}