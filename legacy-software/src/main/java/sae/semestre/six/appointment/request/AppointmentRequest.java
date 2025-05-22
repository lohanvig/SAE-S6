package sae.semestre.six.appointment.request;

import java.time.LocalDateTime;

public class AppointmentRequest {
    private String doctorNumber;
    private String patientNumber;
    private String roomNumber;
    private LocalDateTime appointmentDate;
    private int duration;


    public String getDoctorNumber() {
        return doctorNumber;
    }

    public String getPatientNumber() {
        return patientNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public int getDuration() {
        return duration;
    }
}
