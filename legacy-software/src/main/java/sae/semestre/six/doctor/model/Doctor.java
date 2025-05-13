package sae.semestre.six.doctor.model;

import jakarta.persistence.*;
import sae.semestre.six.appointment.model.Appointment;

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

    @Column(name = "startHour")
    private int workStartHour;

    @Column(name = "endHour")
    private int workEndHour;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private Set<Appointment> appointments;

    
    public Doctor() {
        this.workStartHour = 9;
        this.workEndHour = 17;
    }

    public int getWorkStartHour() {
        return workStartHour;
    }

    public void setWorkStartHour(int workStartHour) {
        if (workStartHour > workEndHour || workStartHour < 0 || workStartHour > 23) {
            throw new IllegalArgumentException("La date de début de travail d'un docteur doit être comprise entre 0 " +
                    "et 23 heure et doit être inférieure à celle de fin.");
        }
        this.workStartHour = workStartHour;
    }

    public int getWorkEndHour() {
        return workEndHour;
    }

    public void setWorkEndHour(int workEndHour) {
        if (workEndHour < workStartHour || workEndHour < 0 || workEndHour > 23) {
            throw new IllegalArgumentException("La date de fin de travail d'un docteur doit être comprise entre 0 " +
                    "et 23 heure et doit être supérieure à celle de début.");
        }
        this.workEndHour = workEndHour;
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

    public Set<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(Set<Appointment> appointments) {
        this.appointments = appointments;
    }
} 