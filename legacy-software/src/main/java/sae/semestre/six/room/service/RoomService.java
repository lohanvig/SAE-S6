package sae.semestre.six.room.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.dao.AppointmentDaoImpl;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.room.model.Room;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RoomService {

    @Autowired
    private AppointmentDaoImpl appointmentDao;

    /**
     * Vérifie si une salle est disponible pour un créneau donné.
     *
     * @param room              La salle concernée
     * @param appointmentDate   La date et heure de début du nouveau rendez-vous
     * @param durationMinutes   La durée du rendez-vous en minutes
     * @return true si la salle est disponible, false s'il y a un chevauchement
     */
    public boolean isRoomAvailable(Room room, LocalDateTime appointmentDate, int durationMinutes) {
        List<Appointment> roomAppointments = appointmentDao.findByRoomIdByDate(room.getId(), appointmentDate);

        LocalDateTime newStart = appointmentDate;
        LocalDateTime newEnd = newStart.plusMinutes(durationMinutes);

        for (Appointment appointment : roomAppointments) {
            LocalDateTime existingStart = appointment.getDate();
            LocalDateTime existingEnd = existingStart.plusMinutes(appointment.getDuration());

            // Vérifie s’il y a chevauchement
            if (newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart)) {
                return false;
            }
        }

        return true;
    }


}
