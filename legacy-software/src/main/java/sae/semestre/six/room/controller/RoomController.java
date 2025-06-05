package sae.semestre.six.room.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sae.semestre.six.appointment.dao.AppointmentDao;
import sae.semestre.six.room.dao.RoomDao;
import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.room.model.Room;
import sae.semestre.six.room.service.RoomService;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Contrôleur REST permettant d’interroger les salles disponibles
 * dans le cadre de la planification de rendez-vous.
 *
 * Il propose des routes pour :
 * - Obtenir la liste des salles disponibles pour un créneau donné
 * - Vérifier la disponibilité d’une salle spécifique
 */
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
     * Récupère les salles disponibles pour un rendez-vous donné.
     * Une salle est considérée disponible si elle n'est pas déjà occupée
     * pendant la période du rendez-vous.
     *
     * @param roomNumber       le numéro de la salle (non utilisé dans le filtrage ici)
     * @param appointmentDate  la date et l’heure de début du rendez-vous
     * @param durationMinutes  la durée du rendez-vous en minutes
     * @return une liste de {@link Map} contenant les informations des salles disponibles :
     *         numéro, capacité, nombre de patients actuels
     */
    @GetMapping("/available")
    public List<Map<String, Object>> getAvailableRooms(@RequestParam String roomNumber,
                                                       @RequestParam LocalDateTime appointmentDate,
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

    /**
     * Vérifie si une salle peut encore accueillir des patients.
     * Cette méthode retourne des informations générales sur la salle,
     * ainsi qu'un booléen indiquant si elle est encore disponible.
     *
     * @param roomNumber le numéro de la salle à vérifier
     * @return une {@link Map} contenant les informations de la salle :
     *         numéro, capacité, patients actuels, et disponibilité
     */
    @GetMapping("/availability")
    public Map<String, Object> getRoomAvailability(@RequestParam String roomNumber) {
        Room room = roomDao.findByRoomNumber(roomNumber);
        Map<String, Object> result = new HashMap<>();

        if (room == null) {
            result.put("error", "Room not found");
            return result;
        }

        result.put("roomNumber", room.getRoomNumber());
        result.put("capacity", room.getCapacity());
        result.put("currentPatients", room.getCurrentPatientCount());
        result.put("available", room.canAcceptPatient());

        return result;
    }
}
