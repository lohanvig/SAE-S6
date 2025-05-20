package sae.semestre.six.room.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sae.semestre.six.appointment.dao.AppointmentDaoImpl;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.room.model.Room;

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
    public boolean isRoomAvailable(Room room, Date appointmentDate, int durationMinutes) {
        List<Appointment> roomAppointments = appointmentDao.findByRoomIdByDate(room.getId(), appointmentDate);

        Calendar startCal = Calendar.getInstance();
        startCal.setTime(appointmentDate);

        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.MINUTE, durationMinutes);

        Date newStart = startCal.getTime();
        Date newEnd = endCal.getTime();

        for (Appointment appointment : roomAppointments) {
            Date existingStart = appointment.getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTime(existingStart);
            cal.add(Calendar.MINUTE, appointment.getDuration());
            Date existingEnd = cal.getTime();

            // Chevauchement
            if (newStart.before(existingEnd) && newEnd.after(existingStart)) {
                return false;
            }
        }

        return true;
    }

}
