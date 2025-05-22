package sae.semestre.six.appointment.request;

import java.time.LocalDateTime;
import java.util.Date;

public class AvailableSlotRequest {

    private String doctorId;
    private LocalDateTime date;

    public String getDoctorId() {
        return doctorId;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
