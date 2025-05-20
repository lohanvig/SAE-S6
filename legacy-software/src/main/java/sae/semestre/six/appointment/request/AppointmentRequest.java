package sae.semestre.six.appointment.request;

import java.util.Date;

public class AppointmentRequest {
    private String doctorNumber;
    private String patientNumber;
    private String roomNumber;
    private Date appointmentDate;
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

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public int getDuration() {
        return duration;
    }
}
