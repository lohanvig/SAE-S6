package sae.semestre.six.timeslot;

import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.model.Doctor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class TimeSlot {
    private LocalDateTime start;
    private LocalDateTime end;

    public TimeSlot(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
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
     * @param doctorAppointments les créneaux du médecin
     * @param date cible pour les créneaux disponibles
     * @return Liste des créneaux horaires disponibles
     */
    public static List<TimeSlot> getDoctorAvailableTimeSlotByDay(LocalDateTime date, Doctor doctor, List<Appointment> doctorAppointments) {
        List<TimeSlot> availableSlots = new ArrayList<>();

        LocalDateTime dayStart = date.toLocalDate().atTime(doctor.getWorkStartHour());
        LocalDateTime dayEnd = date.toLocalDate().atTime(doctor.getWorkEndHour());

        doctorAppointments.sort(Comparator.comparing(Appointment::getDate));

        LocalDateTime slotStart = dayStart;

        for (Appointment appointment : doctorAppointments) {
            LocalDateTime appStart = appointment.getDate();
            LocalDateTime appEnd = appStart.plusMinutes(appointment.getDuration());

            if (slotStart.isBefore(appStart)) {
                availableSlots.add(new TimeSlot(slotStart, appStart));
            }

            if (slotStart.isBefore(appEnd)) {
                slotStart = appEnd;
            }
        }

        if (slotStart.isBefore(dayEnd)) {
            availableSlots.add(new TimeSlot(slotStart, dayEnd));
        }

        return availableSlots;
    }


}

