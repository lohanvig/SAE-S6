package sae.semestre.six.appointment.dto;

import sae.semestre.six.doctor.dto.DoctorShortDto;
import sae.semestre.six.patient.dto.PatientShortDto;
import sae.semestre.six.room.dto.RoomDto;

import java.util.Date;

public class AppointmentDto {
    private Long id;
    private String appointmentNumber;
    private Date date;
    private String status;
    private String description;
    private int duration;

    private PatientShortDto patient;
    private DoctorShortDto doctor;
    private RoomDto room;

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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public PatientShortDto getPatient() {
        return patient;
    }

    public void setPatient(PatientShortDto patient) {
        this.patient = patient;
    }

    public DoctorShortDto getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorShortDto doctor) {
        this.doctor = doctor;
    }

    public RoomDto getRoom() {
        return room;
    }

    public void setRoom(RoomDto room) {
        this.room = room;
    }
}
