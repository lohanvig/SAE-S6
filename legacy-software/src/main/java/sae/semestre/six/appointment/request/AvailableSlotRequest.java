package sae.semestre.six.appointment.request;

import java.util.Date;

public class AvailableSlotRequest {

    private String doctorId;
    private Date date;

    public String getDoctorId() {
        return doctorId;
    }

    public Date getDate() {
        return date;
    }
}
