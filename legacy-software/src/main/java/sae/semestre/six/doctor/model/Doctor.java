package sae.semestre.six.doctor.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import sae.semestre.six.appointment.model.Appointment;

import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doctor_number", unique = true, nullable = false)
    private String doctorNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "specialization")
    private String specialization;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "department")
    private String department;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "start_hour")
    private LocalTime workStartHour;

    @JsonFormat(pattern = "HH:mm")
    @Column(name = "end_hour")
    private LocalTime workEndHour;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private Set<Appointment> appointments;

    public Doctor() {
        this.workStartHour = LocalTime.of(9, 0);
        this.workEndHour = LocalTime.of(17, 0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(String doctorNumber) {
        this.doctorNumber = doctorNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalTime getWorkStartHour() {
        return workStartHour;
    }

    public void setWorkStartHour(LocalTime workStartHour) {
        if (this.workEndHour != null && workStartHour.isAfter(this.workEndHour)) {
            throw new IllegalArgumentException("L'heure de début doit précéder l'heure de fin.");
        }
        this.workStartHour = workStartHour;
    }

    public LocalTime getWorkEndHour() {
        return workEndHour;
    }

    public void setWorkEndHour(LocalTime workEndHour) {
        if (this.workStartHour != null && workEndHour.isBefore(this.workStartHour)) {
            throw new IllegalArgumentException("L'heure de fin doit suivre l'heure de début.");
        }
        this.workEndHour = workEndHour;
    }

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
}
