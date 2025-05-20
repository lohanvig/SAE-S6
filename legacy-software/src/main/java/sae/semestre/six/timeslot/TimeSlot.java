package sae.semestre.six.timeslot;

import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeSlot {
    private Date start;
    private Date end;

    public TimeSlot(Date start, Date end) {
        this.start = start;
        this.end = end;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "De " + start + " à " + end;
    }

    /**
     * Retourne les créneaux horaires disponibles pour un médecin à une date donnée.
     *
     * @param doctor numero du médecin
     * @param doctorAppointment les créneaux du médecin
     * @param date     Date cible pour les créneaux disponibles
     * @return Liste des créneaux horaires disponibles
     */
    public static List<TimeSlot> getDoctorAvailableTimeSlotByDay(Date date, Doctor doctor, List<Appointment> doctorAppointment) {
        List<TimeSlot> availableSlots = new ArrayList<>();

        Calendar dayStart = Calendar.getInstance();
        dayStart.setTime(date);
        dayStart.set(Calendar.HOUR_OF_DAY, doctor.getWorkStartHour());
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);

        Calendar dayEnd = (Calendar) dayStart.clone();
        dayEnd.set(Calendar.HOUR_OF_DAY, doctor.getWorkEndHour());

        Calendar slotStart = (Calendar) dayStart.clone();

        for (Appointment appointment : doctorAppointment) {
            Date appStart = appointment.getDate();
            Calendar appStartCal = Calendar.getInstance();
            appStartCal.setTime(appStart);

            Calendar appEndCal = (Calendar) appStartCal.clone();
            appEndCal.add(Calendar.MINUTE, appointment.getDuration());

            // Si trou entre slotStart et appStartCal
            if (slotStart.before(appStartCal)) {
                availableSlots.add(new TimeSlot(slotStart.getTime(), appStartCal.getTime()));
            }

            // Mettre à jour slotStart pour le prochain tour
            if (slotStart.before(appEndCal)) {
                slotStart = (Calendar) appEndCal.clone();
            }
        }

        // Dernier créneau entre fin dernier rendez-vous et fin journée
        if (slotStart.before(dayEnd)) {
            availableSlots.add(new TimeSlot(slotStart.getTime(), dayEnd.getTime()));
        }

        return availableSlots;
    }
}

