package sae.semestre.six.room.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.dao.AppointmentDao;
import sae.semestre.six.room.dao.RoomDao;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.room.model.Room;
import sae.semestre.six.room.service.RoomService;

import java.util.*;

@RestController
@RequestMapping("/rooms")
public class RoomController {
    
    @Autowired
    private RoomDao roomDao;
    
    @Autowired
    private AppointmentDao appointmentDao;

    @Autowired
    private RoomService roomService;

    /**
     * Route permettant de récupérer les salles disponible à une date donnée.
     *
     * @param appointmentDate La date de début du rendez-vous
     * @param durationMinutes La durée du rendez-vous en minutes
     * @return Liste des salles disponibles pour le créneau donné
     */
    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableRooms(@RequestParam String roomNumber,
                                                       @RequestParam Date appointmentDate,
                                                       @RequestParam int durationMinutes) {
        List<Map<String, Object>> availableRooms = new ArrayList<>();
        List<Room> rooms = roomDao.findAll();

        for (Room room : rooms) {
            boolean isAvailable = roomService.isRoomAvailable(room, appointmentDate, durationMinutes);
            if (isAvailable) {
                Map<String, Object> roomInfo = new HashMap<>();
                roomInfo.put("roomNumber", room.getRoomNumber());
                roomInfo.put("capacity", room.getCapacity());
                roomInfo.put("currentPatients", room.getCurrentPatientCount());
                availableRooms.add(roomInfo);
            }
        }

        return availableRooms;
    }

    @GetMapping("/availability")
    public Map<String, Object> getRoomAvailability(@RequestParam String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);
        if (room == null) {

        }

        Map<String, Object> result = new HashMap<>();
        
        result.put("roomNumber", room.getRoomNumber());
        result.put("capacity", room.getCapacity());
        result.put("currentPatients", room.getCurrentPatientCount());
        result.put("available", room.canAcceptPatient());
        
        return result;
    }
} 