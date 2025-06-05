package sae.semestre.six.appointment.dto;

import sae.semestre.six.appointment.model.Appointment;
import sae.semestre.six.doctor.dto.DoctorShortDto;
import sae.semestre.six.patient.dto.PatientShortDto;
import sae.semestre.six.room.dto.RoomDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire permettant de convertir des entités {@link Appointment}
 * vers des objets {@link AppointmentDto} pour exposer les données côté client.
 */
public class AppointmentMapper {

    /**
     * Convertit une entité {@link Appointment} en un objet {@link AppointmentDto}.
     * Cette méthode effectue également la conversion des objets liés :
     * patient, médecin et salle.
     *
     * @param appointment l'entité {@link Appointment} à convertir
     * @return un objet {@link AppointmentDto} contenant les données à exposer,
     * ou {@code null} si l'entrée est {@code null}
     */
    public static AppointmentDto toDto(Appointment appointment) {
        if (appointment == null) { return null; }

        AppointmentDto dto = new AppointmentDto();
        dto.setId(appointment.getId());
        dto.setAppointmentNumber(appointment.getAppointmentNumber());
        dto.setDate(appointment.getDate());
        dto.setStatus(appointment.getStatus());
        dto.setDescription(appointment.getDescription());
        dto.setDuration(appointment.getDuration());

        PatientShortDto patientDto = new PatientShortDto();
        patientDto.setId(appointment.getPatient().getId());
        patientDto.setFirstName(appointment.getPatient().getFirstName());
        patientDto.setLastName(appointment.getPatient().getLastName());
        patientDto.setPatientNumber(appointment.getPatient().getPatientNumber());
        dto.setPatient(patientDto);

        DoctorShortDto doctorDto = new DoctorShortDto();
        doctorDto.setId(appointment.getDoctor().getId());
        doctorDto.setFirstName(appointment.getDoctor().getFirstName());
        doctorDto.setLastName(appointment.getDoctor().getLastName());
        doctorDto.setSpecialty(appointment.getDoctor().getSpecialization());
        dto.setDoctor(doctorDto);

        RoomDto roomDto = new RoomDto();
        roomDto.setId(appointment.getRoom().getId());
        roomDto.setRoomNumber(appointment.getRoom().getRoomNumber());
        roomDto.setFloor(appointment.getRoom().getFloor());
        roomDto.setType(appointment.getRoom().getType());
        dto.setRoom(roomDto);

        return dto;
    }

    /**
     * Convertit une liste d'entités {@link Appointment} en une liste d'objets {@link AppointmentDto}.
     * Chaque entité est transformée à l'aide de la méthode {@link #toDto(Appointment)}.
     *
     * @param appointments la liste d'entités {@link Appointment} à convertir
     * @return une liste de {@link AppointmentDto} représentant les rendez-vous
     */
    public static List<AppointmentDto> ListToDto(List<Appointment> appointments) {
        ArrayList<AppointmentDto> dtoList = new ArrayList<>();
        for (Appointment appointment:
             appointments) {
            dtoList.add(toDto(appointment));
        }
        return dtoList.stream().toList();
    }
}
